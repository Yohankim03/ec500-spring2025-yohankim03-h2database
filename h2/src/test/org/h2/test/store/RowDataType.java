/*
 * Copyright 2004-2025 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.test.store;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.DataType;

/**
 * A row type.
 */
public class RowDataType extends BasicDataType<Object[]> {

    private final DataType<Object>[] types;

    @SuppressWarnings("unchecked")
    RowDataType(DataType[] types) {
        this.types = types;
    }

    @Override
    public Object[][] createStorage(int size) {
        return new Object[size][];
    }

    @Override
    public int compare(Object[] ax, Object[] bx) {
        if (ax == bx) {
            return 0;
        }
        int al = ax.length;
        int bl = bx.length;
        int len = Math.min(al, bl);
        for (int i = 0; i < len; i++) {
            int comp = types[i].compare(ax[i], bx[i]);
            if (comp != 0) {
                return comp;
            }
        }
        if (len < al) {
            return -1;
        } else if (len < bl) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getMemory(Object[] x) {
        int len = x.length;
        int memory = 0;
        for (int i = 0; i < len; i++) {
            memory += types[i].getMemory(x[i]);
        }
        return memory;
    }

    @Override
    public Object[] read(ByteBuffer buff) {
        int len = DataUtils.readVarInt(buff);
        Object[] x = new Object[len];
        for (int i = 0; i < len; i++) {
            x[i] = types[i].read(buff);
        }
        return x;
    }

    @Override
    public void write(WriteBuffer buff, Object[] x) {
        int len = x.length;
        buff.putVarInt(len);
        for (int i = 0; i < len; i++) {
            types[i].write(buff, x[i]);
        }
    }
}
