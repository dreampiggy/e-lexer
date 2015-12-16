//
//  ll.swift
//  e-lexer
//
//  Created by lizhuoli on 15/12/5.
//  Copyright © 2015年 lizhuoli. All rights reserved.
//

import Foundation

struct Stack<Element> {
    var items = [Element]()
    
    mutating func push(item: Element) {
        items.append(item)
    }
    
    mutating func pop() -> Element {
        return items.removeLast()
    }
    
    func empty() -> Bool {
        return items.isEmpty
    }
    
    func top() -> Element? {
        return items.last
    }
}

class LLParse {
    var stack = Stack<Symbol>()
    let grammar:[Symbol: [Production]]
    var currentChoice = 0
    
    init(grammar:[Symbol: [Production]) {
        self.grammar = grammar
    }
    deinit{}
    
    func ll(currentToken:Character) {
        if (grammar.isEmpty) {
            return
        }
        
        var temp:Symbol?
        var tempNum:Int = 0
        
        let start = grammar["S"]
        stack.push((start?.left)!)
        while(!stack.empty()) {
            guard let top = stack.top() else {return}
            if (top.type == .Terminal) {
                if (top.value == currentToken) {
                    stack.pop()
//                    getNextToken()
                } else {
                    for _ in 0...tempNum {
                        stack.pop()
                    }
                    stack.push(temp!)
                }
            } else if (top.type == .NonTerminal) {
                temp = stack.pop()
                tempNum++
                let rule = nextRule(top)
                if rule.count != 0 {
                    for r in rule {
                        stack.push(r)
                        tempNum++
                    }
                } else {
                    Error.parseError()
                }
            }
        }
    }
    
    
    func nextRule(p :Symbol) -> [Symbol] {
        grammar[p]
    }
}

class LRParse {
    
}