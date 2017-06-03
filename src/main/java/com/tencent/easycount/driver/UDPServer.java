package com.tencent.easycount.driver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPServer {
	public static void main(final String[] args) throws IOException {
		final DatagramSocket ds = new DatagramSocket(5200);
		final byte[] buf = new byte[1000];
		final DatagramPacket ip = new DatagramPacket(buf, buf.length);
		final boolean run = true;
		while (run) {
			Arrays.fill(buf, (byte) 0);
			ds.receive(ip);
			System.out.println(new String(buf).trim());
			if (!run) {
				ds.close();
			}
		}
	}
}
