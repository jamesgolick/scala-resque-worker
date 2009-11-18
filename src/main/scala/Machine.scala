package com.protose.resque
import java.net.InetAddress
import java.lang.management.ManagementFactory

object Machine {
    def hostname = InetAddress.getLocalHost.getHostName
    def pid = ManagementFactory.getRuntimeMXBean.getName.split("@").first
}

// vim: set ts=4 sw=4 et:
