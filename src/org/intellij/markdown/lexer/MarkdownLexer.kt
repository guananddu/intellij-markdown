package org.intellij.markdown.lexer

import org.intellij.markdown.IElementType

import java.io.IOException
import java.io.Reader

import org.intellij.markdown.MarkdownTokenTypes

public class MarkdownLexer(public val originalText: String) {

    private val baseLexer: _MarkdownLexer

    public var type: IElementType? = null
        private set
    private var nextType: IElementType? = null

    public var tokenStart: Int = 0
        private set
    public var tokenEnd: Int = 0
        private set

    {
        baseLexer = _MarkdownLexer(null : Reader?)
        baseLexer.reset(originalText, 0, originalText.length(), 0)

        init()
    }

    public fun advance(): Boolean {
        return locateToken()
    }

    private fun init() {
        type = advanceBase()
        tokenStart = baseLexer.getTokenStart()

        calcNextType()
    }

    private fun locateToken(): Boolean {
        `type` = nextType
        tokenStart = tokenEnd
        if (`type` == null) {
            return false
        }

        calcNextType()
        return true
    }

    private fun calcNextType() {
        do {
            tokenEnd = baseLexer.getTokenEnd()
            nextType = advanceBase()
        } while (nextType == `type` && TOKENS_TO_MERGE.contains(`type`))
    }

    private fun advanceBase(): IElementType? {
        try {
            return baseLexer.advance()
        } catch (e: IOException) {
            e.printStackTrace()
            throw AssertionError("This could not be!")
        }

    }

    class object {
        private val TOKENS_TO_MERGE = setOf(
                MarkdownTokenTypes.TEXT,
                MarkdownTokenTypes.WHITE_SPACE,
                MarkdownTokenTypes.CODE,
                MarkdownTokenTypes.HTML_BLOCK,
                MarkdownTokenTypes.LINK_ID,
                MarkdownTokenTypes.LINK_TITLE,
                MarkdownTokenTypes.URL,
                MarkdownTokenTypes.AUTOLINK,
                MarkdownTokenTypes.EMAIL_AUTOLINK,
                MarkdownTokenTypes.BAD_CHARACTER)
    }
}
