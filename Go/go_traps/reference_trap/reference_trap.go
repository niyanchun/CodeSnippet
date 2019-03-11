package main

import "fmt"

func main() {
	var m map[int]int
	fn(m)

	fmt.Println("In main:", m == nil)
}

func fn(m map[int]int) {
	m = make(map[int]int)
	fmt.Println("In fn:", m == nil)
}
