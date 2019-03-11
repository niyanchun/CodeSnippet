package main

import (
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"io"
	"log"
	"net/http"
	"time"

	"github.com/davecgh/go-spew/spew"
	"github.com/gorilla/mux"
)

// Message by POST
type Message struct {
	BPM int
}

// Block in Blockchain
type Block struct {
	Index     int    // 节点在区块链中的位置
	Timestamp string // 数据写入的时间戳
	BPM       int    // 每分钟心跳数
	Hash      string // 本节点数据哈希值
	PrevHash  string // 前一个节点哈希值
}

// BlockChain is Block slice
var BlockChain []Block

func main() {
	go func() {
		t := time.Now()
		genesisBlock := Block{0, t.String(), 0, "", ""}
		spew.Dump(genesisBlock)
		BlockChain = append(BlockChain, genesisBlock)
	}()
	log.Fatal(run())
}

// 计算节点哈希值
func calculateHash(block Block) string {
	// 使用节点除`Hash`字段之外的其他字段计算本节点哈希值
	record := string(block.Index) + block.Timestamp + string(block.BPM) + block.PrevHash
	h := sha256.New()
	h.Write([]byte(record))
	hashed := h.Sum(nil)
	return hex.EncodeToString(hashed)
}

// 生成新的节点
func generateBlock(oldBlock Block, BPM int) (Block, error) {
	var newBlock Block

	t := time.Now()

	newBlock.Index = oldBlock.Index + 1
	newBlock.BPM = BPM
	newBlock.Timestamp = t.String()
	newBlock.PrevHash = oldBlock.Hash
	newBlock.Hash = calculateHash(newBlock)

	return newBlock, nil
}

// 判断节点合法性
func isBlockValid(newBlock, oldBlock Block) bool {
	// 校验位置是否连续
	if oldBlock.Index+1 != newBlock.Index {
		return false
	}
	// 节点间哈希值校验
	if oldBlock.Hash != newBlock.PrevHash {
		return false
	}
	// 本节点哈希值校验
	if calculateHash(newBlock) != newBlock.Hash {
		return false
	}

	return true
}

// 冲突处理。如果有多个链的时候，我们认为较长的链是最新的链
func replaceChain(newBlocks []Block) {
	if len(newBlocks) > len(BlockChain) {
		BlockChain = newBlocks
	}
}

func run() error {
	mux := makeMuxRouter()
	s := &http.Server{
		Addr:           ":8080",
		Handler:        mux,
		ReadTimeout:    10 * time.Second,
		WriteTimeout:   10 * time.Second,
		MaxHeaderBytes: 1 << 20,
	}

	if err := s.ListenAndServe(); err != nil {
		return err
	}
	return nil
}

func makeMuxRouter() http.Handler {
	muxRouter := mux.NewRouter()
	muxRouter.HandleFunc("/", handleGetBlockChain).Methods("GET")
	muxRouter.HandleFunc("/", handleWriteBlockChain).Methods("POST")

	return muxRouter
}

func handleGetBlockChain(w http.ResponseWriter, r *http.Request) {
	bytes, err := json.MarshalIndent(BlockChain, "", "	")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	io.WriteString(w, string(bytes))
}

func handleWriteBlockChain(w http.ResponseWriter, r *http.Request) {
	var m Message

	decoder := json.NewDecoder(r.Body)
	if err := decoder.Decode(&m); err != nil {
		respondWithJSON(w, r, http.StatusInternalServerError, r.Body)
		return
	}
	defer r.Body.Close()

	newBlock, err := generateBlock(BlockChain[len(BlockChain)-1], m.BPM)
	if err != nil {
		respondWithJSON(w, r, http.StatusInternalServerError, m)
		return
	}

	if isBlockValid(newBlock, BlockChain[len(BlockChain)-1]) {
		newBlockChain := append(BlockChain, newBlock)
		replaceChain(newBlockChain)
		spew.Dump(BlockChain)
	}

	respondWithJSON(w, r, http.StatusCreated, newBlock)
}

func respondWithJSON(w http.ResponseWriter, r *http.Request, code int, payload interface{}) {
	response, err := json.MarshalIndent(payload, "", "	")
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte("HTTP 500: Internal Server Error"))
		return
	}
	w.WriteHeader(code)
	w.Write(response)
}
