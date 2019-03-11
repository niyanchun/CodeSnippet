package main

import (
	"bufio"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"io"
	"log"
	"net"
	"strconv"
	"sync"
	"time"

	"github.com/davecgh/go-spew/spew"
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

// bcServer handles incoming concurrent Blocks
var bcServer chan []Block

var mutex = &sync.Mutex{}

func main() {
	// make unbuffered channel
	bcServer = make(chan []Block)

	// 创建区块链中的初始块
	t := time.Now()
	genesisBlock := Block{0, t.String(), 0, "", ""}
	spew.Dump(genesisBlock)
	BlockChain = append(BlockChain, genesisBlock)

	// 创建TCP服务器
	server, err := net.Listen("tcp", ":9000")
	if err != nil {
		log.Fatal(err)
	}
	defer server.Close()

	// 监听
	for {
		conn, err := server.Accept()
		if err != nil {
			log.Fatal(err)
		}
		go handleConn(conn)
	}
}

func handleConn(conn net.Conn) {
	defer conn.Close()

	io.WriteString(conn, "Enter a new BPM:")

	scanner := bufio.NewScanner(conn)

	go func() {
		for scanner.Scan() {
			// 处理用户输入
			bpm, err := strconv.Atoi(scanner.Text())
			if err != nil {
				log.Printf("%v is not a number: %v", scanner.Text(), err)
				continue
			}
			// 生成新的块
			newBlock, err := generateBlock(BlockChain[len(BlockChain)-1], bpm)
			if err != nil {
				log.Println(err)
				continue
			}
			// 判断新块的合法性，并合并可能的链
			if isBlockValid(newBlock, BlockChain[len(BlockChain)-1]) {
				newBlockChain := append(BlockChain, newBlock)
				replaceChain(newBlockChain)
			}

			// 把新的区块链放到channel中，后面广播给所有的节点
			bcServer <- BlockChain
			io.WriteString(conn, "\nEnter a new BPM:")
		}
	}()

	go func() {
		for {
			time.Sleep(30 * time.Second)
			mutex.Lock()
			output, err := json.Marshal(BlockChain)
			if err != nil {
				log.Fatal(err)
			}
			mutex.Unlock()
			io.WriteString(conn, string(output))
		}
	}()

	for _ = range bcServer {
		spew.Dump(BlockChain)
	}
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
