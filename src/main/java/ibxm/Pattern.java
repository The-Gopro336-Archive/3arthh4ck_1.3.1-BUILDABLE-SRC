/*
 * Decompiled with CFR 0.150.
 */
package ibxm;

public class Pattern {
    public int num_rows = 1;
    private int data_offset;
    private int note_index;
    private byte[] pattern_data;

    public Pattern() {
        this.set_pattern_data(new byte[0]);
    }

    public void set_pattern_data(byte[] data) {
        if (data != null) {
            this.pattern_data = data;
        }
        this.data_offset = 0;
        this.note_index = 0;
    }

    public void get_note(int[] note, int index) {
        if (index < this.note_index) {
            this.note_index = 0;
            this.data_offset = 0;
        }
        while (this.note_index <= index) {
            this.data_offset = this.next_note(this.data_offset, note);
            ++this.note_index;
        }
    }

    public int next_note(int data_offset, int[] note) {
        if (data_offset < 0) {
            data_offset = this.pattern_data.length;
        }
        int bitmask = 128;
        if (data_offset < this.pattern_data.length) {
            bitmask = this.pattern_data[data_offset] & 0xFF;
        }
        if ((bitmask & 0x80) == 128) {
            ++data_offset;
        } else {
            bitmask = 31;
        }
        for (int field = 0; field < 5; ++field) {
            note[field] = 0;
            if ((bitmask & 1) == 1 && data_offset < this.pattern_data.length) {
                note[field] = this.pattern_data[data_offset] & 0xFF;
                ++data_offset;
            }
            bitmask >>= 1;
        }
        return data_offset;
    }
}

