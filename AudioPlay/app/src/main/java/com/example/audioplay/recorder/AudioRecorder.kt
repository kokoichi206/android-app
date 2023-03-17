package com.example.audioplay.recorder

import java.io.File

interface AudioRecorder {

    fun start(outputFile: File)
    fun stop()
}