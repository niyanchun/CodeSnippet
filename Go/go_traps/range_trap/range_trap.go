package main

import (
	"fmt"
	"strconv"
)

func main() {
	// slice can not add in iteration
	fmt.Println("Slice Test:")
	v := []int{1, 2, 3}
	for _, i := range v {
		fmt.Println(i)
		v = append(v, i)
	}

	fmt.Println("\nMap Test:")
	// map may or may not add in iteration
	m := map[string]int{"1": 1, "2": 2, "100": 100, "1000": 1000}
	i := 5
	for key, value := range m {
		fmt.Println("key:", key, "value:", value)
		m[strconv.Itoa(i)] = i
		i = i * i
	}
}
