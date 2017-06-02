package com.tencent.easycount.driver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPServer {
	public static void main(String[] args) throws IOException {
		DatagramSocket ds = new DatagramSocket(5200);
		byte[] buf = new byte[1000];
		DatagramPacket ip = new DatagramPacket(buf, buf.length);
		while (true) {
			Arrays.fill(buf, (byte) 0);
			ds.receive(ip);
			System.out.println(new String(buf).trim());
		}
	}
}
