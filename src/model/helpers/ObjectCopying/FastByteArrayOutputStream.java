package model.helpers.ObjectCopying;

import java.io.InputStream;
import java.io.OutputStream;

public class FastByteArrayOutputStream extends OutputStream {
	/**
	 * буфер та розмір
	 */
	protected byte[] buf = null;
	protected int size = 0;

	/**
	 * Створює потік з розміром буферу 5 кілобайт
	 */
	public FastByteArrayOutputStream() {
		this(5 * 1024);
	}

	/**
	 * Створює потік з переданим розміом буферу
	 */
	public FastByteArrayOutputStream(int initSize) {
		this.size = 0;
		this.buf = new byte[initSize];
	}

	/**
	 * Впевнюємося що маємо достатньо великий буфер для переданого розміру
	 */
	private void verifyBufferSize(int sz) {
		if (sz > buf.length) {
			byte[] old = buf;
			buf = new byte[Math.max(sz, 2 * buf.length)];
			System.arraycopy(old, 0, buf, 0, old.length);
			old = null;
		}
	}

	public int getSize() {
		return size;
	}

	/**
	 * Повертає масив байтів з записаними даними. Зауважте, що розмір цього
	 * масиву завжди буде більшим ніж кількість даних, що в ньому записана
	 */
	public byte[] getByteArray() {
		return buf;
	}

	public final void write(byte b[]) {
		verifyBufferSize(size + b.length);
		System.arraycopy(b, 0, buf, size, b.length);
		size += b.length;
	}

	public final void write(byte b[], int off, int len) {
		verifyBufferSize(size + len);
		System.arraycopy(b, off, buf, size, len);
		size += len;
	}

	public final void write(int b) {
		verifyBufferSize(size + 1);
		buf[size++] = (byte) b;
	}

	public void reset() {
		size = 0;
	}

	/**
	 * Повертає вхідний байтовий потік для зчитування написаних даних назад
	 */
	public InputStream getInputStream() {
		return new FastByteArrayInputStream(buf, size);
	}

}