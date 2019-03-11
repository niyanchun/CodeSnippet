package main

import (
	"fmt"
)

func main() {
	var s []int
	for i := 1; i <= 6; i++ {
		s = append(s, i)
	}

	// quiz 1
	// reverse1(s)

	// quiz 2
	// reverse2(s)

	// quiz 3
	// reverse3(s)

	// quiz 4
	reverse4(s)

	fmt.Println(s)
}

func reverse1(s []int) {
	for i, j := 0, len(s); i < j; i++ {
		j--
		s[i], s[j] = s[j], s[i]
	}
}

func reverse2(s []int) {
	s = append(s, 999)
	for i, j := 0, len(s); i < j; i++ {
		j--
		s[i], s[j] = s[j], s[i]
	}
}

func reverse3(s []int) {
	s = append(s, 999, 1000, 1001)
	for i, j := 0, len(s); i < j; i++ {
		j--
		s[i], s[j] = s[j], s[i]
	}
}

func reverse4(s []int) {
	newElem := 999
	for len(s) < cap(s) {
		fmt.Println("append new element:", newElem, "cap:", cap(s), "\tlen:", len(s))
		s = append(s, newElem)
		newElem++
	}

	for i, j := 0, len(s); i < j; i++ {
		j--
		s[i], s[j] = s[j], s[i]
	}
}
