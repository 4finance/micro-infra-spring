package com.ofg.stub.server

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class AvailablePortScanner {

    private final int minPortNumber
    private final int maxPortNumber
    private int portToScan

    AvailablePortScanner(int minPortNumber, int maxPortNumber) {
        this.portToScan = minPortNumber
        this.minPortNumber = minPortNumber
        this.maxPortNumber = maxPortNumber
    }

    int nextAvailablePort() {
        ServerSocket socket = null
        while (portToScan <= maxPortNumber) {
            try {
                socket = new ServerSocket(portToScan)
                return portToScan
            } catch (IOException e) {
                log.debug("Port $portToScan not available")
            } finally {
                closeSocket(socket)
                portToScan++
            }
        }
        throw new NoPortAvailableException(minPortNumber, maxPortNumber)
    }

    private void closeSocket(ServerSocket socket) {
        if (socket) {
            try {
                socket.close()
            } catch (IOException e) {
                log.debug("Failed to close socket listening at port $portToScan")
            }
        }
    }

    private static class NoPortAvailableException extends RuntimeException {
        NoPortAvailableException(int loweBound, int upperBound) {
            super("Could not find available port in range $loweBound:$upperBound")
        }
    }
}
