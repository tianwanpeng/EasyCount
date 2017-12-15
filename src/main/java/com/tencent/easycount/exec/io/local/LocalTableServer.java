package com.tencent.easycount.exec.io.local;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;

public abstract class LocalTableServer implements LocalModeProtocol {
	protected RPC.Server server;
	private final String bindAddress;
	private final int port;

	private final Table tbl;

	private final String printBindAddress;
	private final int printPort;

	private DatagramSocket ds;

	public LocalTableServer(final Table tbl, final ECConfiguration config) {
		this.tbl = tbl;
		this.bindAddress = TableUtils.getLocalTableAddr(tbl);
		this.port = TableUtils.getLocalTablePort(tbl);

		try {
			final RPC.Builder bd = new RPC.Builder(new Configuration());
			bd.setProtocol(LocalTableServer.class);
			bd.setInstance(this);
			bd.setBindAddress(this.bindAddress);
			bd.setPort(this.port);
			this.server = bd.build();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		this.printBindAddress = config.get("local.table.print.addr",
				"127.0.0.1");
		this.printPort = config.getInt("local.table.print.port", 5200);

		try {
			this.ds = new DatagramSocket();
		} catch (final SocketException e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() {
		this.server.start();
	}

	@Override
	public long getProtocolVersion(final String arg0, final long arg1)
			throws IOException {
		return versionID;
	}

	protected void print(final String key, final Writable data,
			final String prefix) {
		final StringBuffer sb = new StringBuffer();
		sb.append(this.tbl.getTableName()).append(",").append(prefix)
		.append(",");
		if (key != null) {
			sb.append(key).append("-");
		}
		sb.append(data.toString());
		final byte[] buf = sb.toString().getBytes();
		try {
			final DatagramPacket dp = new DatagramPacket(buf, buf.length,
					InetAddress.getByName(this.printBindAddress),
					this.printPort);
			this.ds.send(dp);
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
