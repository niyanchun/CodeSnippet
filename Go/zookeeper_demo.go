package main

import "github.com/samuel/go-zookeeper/zk"
import "time"
import "fmt"

func main() {
	c, _, err := zk.Connect([]string{"10.9.1.13"}, time.Second)
	if err != nil {
		panic(err)
	}

	children, stat, ch, err := c.ChildrenW("/")
	if err != nil {
		panic(err)
	}

	fmt.Printf("%+v %+v\n", children, stat)
	e := <-ch
	fmt.Printf("%+v\n", e)
}
