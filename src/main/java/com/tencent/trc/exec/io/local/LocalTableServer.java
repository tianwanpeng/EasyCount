package com.tencent.trc.exec.io.local;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.TableUtils;

public abstract class LocalTableServer implements LocalModeProtocol {
	protected RPC.Server server;
	private String bindAddress;
	private int port;

	private Table tbl;

	private String printBindAddress;
	private int printPort;

	private DatagramSocket ds;

	public LocalTableServer(Table tbl, TrcConfiguration config) {
		this.tbl = tbl;
		this.bindAddress = TableUtils.getLocalTableAddr(tbl);
		this.port = TableUtils.getLocalTablePort(tbl);

		try {
			server = RPC
					.getServer(this, bindAddress, port, new Configuration());
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.printBindAddress = config.get("local.table.print.addr",
				"127.0.0.1");
		this.printPort = config.getInt("local.table.print.port", 5200);

		try {
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() {
		this.server.start();
	}

	@Override
	public long getProtocolVersion(String arg0, long arg1) throws IOException {
		return versionID;
	}

	protected void print(String key, Writable data, String prefix) {
		StringBuffer sb = new StringBuffer();
		sb.append(tbl.getTableName()).append(",").append(prefix).append(",");
		if (key != null) {
			sb.append(key).append("-");
		}
		sb.append(data.toString());
		byte[] buf = sb.toString().getBytes();
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length,
					InetAddress.getByName(printBindAddress), printPort);
			ds.send(dp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
