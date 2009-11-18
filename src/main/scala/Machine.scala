package com.protose.resque
import java.net.InetAddress

object Machine {
    def hostname = InetAddress.getLocalHost.getHostName
}

// vim: set ts=4 sw=4 et:
