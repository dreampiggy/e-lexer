//
//  Scanner.swift
//  e-lexer
//
//  Created by lizhuoli on 15/12/5.
//  Copyright © 2015年 lizhuoli. All rights reserved.
//

import Foundation

class Scanner {
    let file:StreamReader
    var line:String?
    var characters:String.CharacterView?
    var char:Character?
    var lineNum:Int = 0
    
    init(path:String) {
        guard let streamReader = StreamReader(path: path) else {
            let fileManager = NSFileManager.defaultManager()
            let currentDir = fileManager.currentDirectoryPath
            print("Do not found input.l file at \(currentDir)")
            fatalError()
        }
        file = streamReader
        print("Read the input .l file")
    }
    
    deinit {
        file.close()
    }
    
    func getCurrentChar() -> Character? {
        return characters?.last
    }
    
    func getNextChar() -> Character? {
        return characters?.popFirst()
    }
    
    func getNextLine() -> String? {
        if let newLine = file.nextLine() {
            line = newLine.stringByReplacingOccurrencesOfString(" ", withString: "")
            line = line! + "\n"
            characters = line?.characters
            lineNum++
            print("New line")
            return line
        } else {
            print("No more line")
            return nil
        }
    }
    
    func getLineNum() -> Int {
        return lineNum
    }
    
}