package com.hotels.intellij.plugins.network.converter

fun Map<String, String>.toDisplayString() = entries.joinToString("\n") { entry: Map.Entry<String, String> -> "${entry.key}: ${entry.value}" }