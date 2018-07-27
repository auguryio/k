package com.elvarg.game.collision;

/**
 * Represents a buffer.
 * 
 * Taken from the 317 client.
 * 
 * @author Professor Oak
 */
public class Buffer {

	private byte[] buffer;
	public int offset;

	public Buffer(byte[] buffer) {
		this.buffer = buffer;
		this.offset = 0;
	}

	public void skip(int length) {
		offset += length;
	}

	public void setOffset(int location) {
		offset = location;
	}

	public void setOffset(long location) {
		offset = (int) location;
	}

	public int length() {
		return buffer.length;
	}

	public byte getByte() {
		return buffer[offset++];
	}

	public int getUByte() {
		return buffer[offset++] & 0xff;
	}

	public int getShort() {
		int val = (getByte() << 8) + getByte();
		if (val > 32767) {
			val -= 0x10000;
		}
		return val;
	}

	public int getUShort() {
		return (getUByte() << 8) + getUByte();
	}

	public int getInt() {
		return (getUByte() << 24) + (getUByte() << 16) + (getUByte() << 8) + getUByte();
	}

	public long getLong() {
		return (getUByte() << 56) + (getUByte() << 48) + (getUByte() << 40) + (getUByte() << 32) + (getUByte() << 24) + (getUByte() << 16) + (getUByte() << 8) + getUByte();
	}

	public int readUnsignedWord() {
		offset += 2;
		return ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] & 0xff);
	}

	public int getUSmart() {
		int i = buffer[offset] & 0xff;
		if (i < 128) {
			return getUByte();
		} else {
			return getUShort() - 32768;
		}
	}	
    public int readSmart() {
        try {
            int value = 0;
            int ptr;
            for (ptr = getUSmart(); 32767 == ptr; ptr = getUSmart())
                value += 32767;
            value += ptr;
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

	public String getString() {
		int i = offset;
		while (buffer[offset++] != 10)
			;
		return new String(buffer, i, offset - i - 1);
	}

	public byte[] getBytes() {
		int i = offset;
		while (buffer[offset++] != 10)
			;
		byte abyte0[] = new byte[offset - i - 1];
		System.arraycopy(buffer, i, abyte0, i - i, offset - 1 - i);
		return abyte0;
	}

	public byte[] read(int length) {
		byte[] b = new byte[length];
		for (int i = 0; i < length; i++) {
			b[i] = buffer[offset++];
		}
		return b;
	}
}
