//
//  production.swift
//  e-lexer
//
//  Created by lizhuoli on 15/12/5.
//  Copyright © 2015年 lizhuoli. All rights reserved.
//

import Foundation

enum SymbolType:Int {
    case Terminal = 1
    case NonTerminal = 0
}

struct Symbol: Equatable {
    init(value: Character, type: SymbolType) {
        self.value = value
        self.type = type
    }
    var value:Character
    var type:SymbolType
}

func ==(lhs: Symbol, rhs: Symbol) -> Bool {
    return lhs.value == rhs.value && lhs.type == rhs.type
}

class Production: Hashable {
    init(){
    }
    deinit{
    }
    var left:Symbol?
    var right:[Symbol] = []
    
    var hashValue: Int {
        get {
            return "\(self.left), \(self.right)".hashValue
        }
    }
}


func ==(lhs: Production, rhs: Production) -> Bool {
    return lhs.left == lhs.left && lhs.right == rhs.right
}