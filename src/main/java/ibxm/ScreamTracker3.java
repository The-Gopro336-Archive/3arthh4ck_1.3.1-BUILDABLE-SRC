/*
 * Decompiled with CFR 0.150.
 */
package ibxm;

import ibxm.Instrument;
import ibxm.Module;
import ibxm.Pattern;
import ibxm.Sample;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ScreamTracker3 {
    private static final int[] effect_map = new int[]{255, 37, 11, 13, 10, 2, 1, 3, 4, 29, 0, 6, 5, 255, 255, 9, 255, 27, 7, 14, 15, 36, 16, 255, 255, 255, 255, 255, 255, 255, 255, 255};
    private static final int[] effect_s_map = new int[]{0, 3, 5, 4, 7, 255, 255, 255, 8, 255, 9, 6, 12, 13, 14, 15};

    public static boolean is_s3m(byte[] header_96_bytes) {
        String s3m_identifier = ScreamTracker3.ascii_text(header_96_bytes, 44, 4);
        return s3m_identifier.equals("SCRM");
    }

    public static Module load_s3m(byte[] header_96_bytes, DataInput data_input) throws IOException {
        int channel_config;
        int channel_idx;
        byte[] s3m_file = ScreamTracker3.read_s3m_file(header_96_bytes, data_input);
        Module module = new Module();
        module.song_title = ScreamTracker3.ascii_text(s3m_file, 0, 28);
        int num_pattern_orders = ScreamTracker3.get_num_pattern_orders(s3m_file);
        int num_instruments = ScreamTracker3.get_num_instruments(s3m_file);
        int num_patterns = ScreamTracker3.get_num_patterns(s3m_file);
        int flags = ScreamTracker3.unsigned_short_le(s3m_file, 38);
        int tracker_version = ScreamTracker3.unsigned_short_le(s3m_file, 40);
        if ((flags & 0x40) == 64 || tracker_version == 4864) {
            module.fast_volume_slides = true;
        }
        boolean signed_samples = false;
        if (ScreamTracker3.unsigned_short_le(s3m_file, 42) == 1) {
            signed_samples = true;
        }
        module.global_volume = s3m_file[48] & 0xFF;
        module.default_speed = s3m_file[49] & 0xFF;
        module.default_tempo = s3m_file[50] & 0xFF;
        int master_volume = s3m_file[51] & 0x7F;
        module.channel_gain = master_volume << 15 >> 7;
        boolean stereo_mode = (s3m_file[51] & 0x80) == 128;
        boolean default_panning = (s3m_file[53] & 0xFF) == 252;
        int[] channel_map = new int[32];
        int num_channels = 0;
        for (channel_idx = 0; channel_idx < 32; ++channel_idx) {
            channel_config = s3m_file[64 + channel_idx] & 0xFF;
            channel_map[channel_idx] = -1;
            if (channel_config >= 16) continue;
            channel_map[channel_idx] = num_channels++;
        }
        module.set_num_channels(num_channels);
        int panning_offset = 96 + num_pattern_orders + num_instruments * 2 + num_patterns * 2;
        for (channel_idx = 0; channel_idx < 32; ++channel_idx) {
            channel_config = s3m_file[64 + channel_idx] & 0xFF;
            if (channel_map[channel_idx] < 0) continue;
            int panning = 128;
            if (stereo_mode) {
                panning = channel_config < 8 ? 64 : 192;
            }
            if (default_panning && ((flags = s3m_file[panning_offset + channel_idx] & 0xFF) & 0x20) == 32) {
                panning = (flags & 0xF) << 4;
            }
            module.set_initial_panning(channel_map[channel_idx], panning);
        }
        int[] sequence = ScreamTracker3.read_s3m_sequence(s3m_file);
        module.set_sequence_length(sequence.length);
        for (int order_idx = 0; order_idx < sequence.length; ++order_idx) {
            module.set_sequence(order_idx, sequence[order_idx]);
        }
        module.set_num_instruments(num_instruments);
        for (int instrument_idx = 0; instrument_idx < num_instruments; ++instrument_idx) {
            Instrument instrument = ScreamTracker3.read_s3m_instrument(s3m_file, instrument_idx, signed_samples);
            module.set_instrument(instrument_idx + 1, instrument);
        }
        module.set_num_patterns(num_patterns);
        for (int pattern_idx = 0; pattern_idx < num_patterns; ++pattern_idx) {
            module.set_pattern(pattern_idx, ScreamTracker3.read_s3m_pattern(s3m_file, pattern_idx, channel_map));
        }
        return module;
    }

    private static int[] read_s3m_sequence(byte[] s3m_file) {
        int pattern_order;
        int order_idx;
        int num_pattern_orders = ScreamTracker3.get_num_pattern_orders(s3m_file);
        int sequence_length = 0;
        for (order_idx = 0; order_idx < num_pattern_orders && (pattern_order = s3m_file[96 + order_idx] & 0xFF) != 255; ++order_idx) {
            if (pattern_order >= 254) continue;
            ++sequence_length;
        }
        int[] sequence = new int[sequence_length];
        int sequence_idx = 0;
        for (order_idx = 0; order_idx < num_pattern_orders && (pattern_order = s3m_file[96 + order_idx] & 0xFF) != 255; ++order_idx) {
            if (pattern_order >= 254) continue;
            sequence[sequence_idx] = pattern_order;
            ++sequence_idx;
        }
        return sequence;
    }

    private static Instrument read_s3m_instrument(byte[] s3m_file, int instrument_idx, boolean signed_samples) {
        int instrument_offset = ScreamTracker3.get_instrument_offset(s3m_file, instrument_idx);
        Instrument instrument = new Instrument();
        instrument.name = ScreamTracker3.ascii_text(s3m_file, instrument_offset + 48, 28);
        Sample sample = new Sample();
        if (s3m_file[instrument_offset] == 1) {
            int sample_data_length = ScreamTracker3.get_sample_data_length(s3m_file, instrument_offset);
            int loop_start = ScreamTracker3.unsigned_short_le(s3m_file, instrument_offset + 20);
            int loop_length = ScreamTracker3.unsigned_short_le(s3m_file, instrument_offset + 24) - loop_start;
            sample.volume = s3m_file[instrument_offset + 28] & 0xFF;
            if (s3m_file[instrument_offset + 30] != 0) {
                throw new IllegalArgumentException("ScreamTracker3: Packed samples not supported!");
            }
            if ((s3m_file[instrument_offset + 31] & 1) == 0) {
                loop_length = 0;
            }
            sample.c2_rate = ScreamTracker3.unsigned_short_le(s3m_file, instrument_offset + 32);
            short[] sample_data = new short[sample_data_length];
            int sample_data_offset = ScreamTracker3.get_sample_data_offset(s3m_file, instrument_offset);
            if (signed_samples) {
                for (int sample_idx = 0; sample_idx < sample_data_length; ++sample_idx) {
                    int amplitude = s3m_file[sample_data_offset + sample_idx] << 8;
                    sample_data[sample_idx] = (short)(amplitude << 8);
                }
            } else {
                for (int sample_idx = 0; sample_idx < sample_data_length; ++sample_idx) {
                    int amplitude = (s3m_file[sample_data_offset + sample_idx] & 0xFF) - 128;
                    sample_data[sample_idx] = (short)(amplitude << 8);
                }
            }
            sample.set_sample_data(sample_data, loop_start, loop_length, false);
        }
        instrument.set_num_samples(1);
        instrument.set_sample(0, sample);
        return instrument;
    }

    private static Pattern read_s3m_pattern(byte[] s3m_file, int pattern_idx, int[] channel_map) {
        int channel_idx;
        int num_channels = 0;
        for (channel_idx = 0; channel_idx < 32; ++channel_idx) {
            if (channel_map[channel_idx] < num_channels) continue;
            num_channels = channel_idx + 1;
        }
        int num_notes = num_channels * 64;
        byte[] pattern_data = new byte[num_notes * 5];
        int row_idx = 0;
        int pattern_offset = ScreamTracker3.get_pattern_offset(s3m_file, pattern_idx) + 2;
        while (row_idx < 64) {
            int token = s3m_file[pattern_offset] & 0xFF;
            ++pattern_offset;
            if (token > 0) {
                channel_idx = channel_map[token & 0x1F];
                int note_idx = (num_channels * row_idx + channel_idx) * 5;
                if ((token & 0x20) == 32) {
                    if (channel_idx >= 0) {
                        int key = s3m_file[pattern_offset] & 0xFF;
                        if (key == 255) {
                            key = 0;
                        } else if (key == 254) {
                            key = 97;
                        } else {
                            for (key = ((key & 0xF0) >> 4) * 12 + (key & 0xF) + 1; key > 96; key -= 12) {
                            }
                        }
                        pattern_data[note_idx] = (byte)key;
                        pattern_data[note_idx + 1] = s3m_file[pattern_offset + 1];
                    }
                    pattern_offset += 2;
                }
                if ((token & 0x40) == 64) {
                    if (channel_idx >= 0) {
                        int volume_column = (s3m_file[pattern_offset] & 0xFF) + 16;
                        pattern_data[note_idx + 2] = (byte)volume_column;
                    }
                    ++pattern_offset;
                }
                if ((token & 0x80) != 128) continue;
                if (channel_idx >= 0) {
                    int effect = s3m_file[pattern_offset] & 0xFF;
                    int effect_param = s3m_file[pattern_offset + 1] & 0xFF;
                    if ((effect = effect_map[effect & 0x1F]) == 255) {
                        effect = 0;
                        effect_param = 0;
                    }
                    if (effect == 14) {
                        effect = effect_s_map[(effect_param & 0xF0) >> 4];
                        effect_param &= 0xF;
                        switch (effect) {
                            case 8: {
                                effect = 8;
                                effect_param <<= 4;
                                break;
                            }
                            case 9: {
                                effect = 8;
                                if (effect_param > 7) {
                                    effect_param -= 8;
                                    break;
                                }
                                effect_param += 8;
                                break;
                            }
                            case 255: {
                                effect = 0;
                                effect_param = 0;
                                break;
                            }
                            default: {
                                effect_param = (effect & 0xF) << 4 | effect_param & 0xF;
                                effect = 14;
                            }
                        }
                    }
                    pattern_data[note_idx + 3] = (byte)effect;
                    pattern_data[note_idx + 4] = (byte)effect_param;
                }
                pattern_offset += 2;
                continue;
            }
            ++row_idx;
        }
        Pattern pattern = new Pattern();
        pattern.num_rows = 64;
        pattern.set_pattern_data(pattern_data);
        return pattern;
    }

    private static byte[] read_s3m_file(byte[] header_96_bytes, DataInput data_input) throws IOException {
        int pattern_offset;
        int pattern_idx;
        int instrument_offset;
        int instrument_idx;
        if (!ScreamTracker3.is_s3m(header_96_bytes)) {
            throw new IllegalArgumentException("ScreamTracker3: Not an S3M file!");
        }
        byte[] s3m_file = header_96_bytes;
        int s3m_file_length = header_96_bytes.length;
        int num_pattern_orders = ScreamTracker3.get_num_pattern_orders(s3m_file);
        int num_instruments = ScreamTracker3.get_num_instruments(s3m_file);
        int num_patterns = ScreamTracker3.get_num_patterns(s3m_file);
        s3m_file_length += num_pattern_orders;
        s3m_file_length += num_instruments * 2;
        s3m_file = ScreamTracker3.read_more(s3m_file, s3m_file_length += num_patterns * 2, data_input);
        for (instrument_idx = 0; instrument_idx < num_instruments; ++instrument_idx) {
            instrument_offset = ScreamTracker3.get_instrument_offset(s3m_file, instrument_idx);
            if ((instrument_offset += 80) <= s3m_file_length) continue;
            s3m_file_length = instrument_offset;
        }
        for (pattern_idx = 0; pattern_idx < num_patterns; ++pattern_idx) {
            pattern_offset = ScreamTracker3.get_pattern_offset(s3m_file, pattern_idx);
            if ((pattern_offset += 2) <= s3m_file_length) continue;
            s3m_file_length = pattern_offset;
        }
        s3m_file = ScreamTracker3.read_more(s3m_file, s3m_file_length, data_input);
        for (instrument_idx = 0; instrument_idx < num_instruments; ++instrument_idx) {
            instrument_offset = ScreamTracker3.get_instrument_offset(s3m_file, instrument_idx);
            int sample_data_offset = ScreamTracker3.get_sample_data_offset(s3m_file, instrument_offset);
            if ((sample_data_offset += ScreamTracker3.get_sample_data_length(s3m_file, instrument_offset)) <= s3m_file_length) continue;
            s3m_file_length = sample_data_offset;
        }
        for (pattern_idx = 0; pattern_idx < num_patterns; ++pattern_idx) {
            pattern_offset = ScreamTracker3.get_pattern_offset(s3m_file, pattern_idx);
            pattern_offset += ScreamTracker3.get_pattern_length(s3m_file, pattern_offset);
            if ((pattern_offset += 2) <= s3m_file_length) continue;
            s3m_file_length = pattern_offset;
        }
        s3m_file = ScreamTracker3.read_more(s3m_file, s3m_file_length, data_input);
        return s3m_file;
    }

    private static int get_num_pattern_orders(byte[] s3m_file) {
        int num_pattern_orders = ScreamTracker3.unsigned_short_le(s3m_file, 32);
        return num_pattern_orders;
    }

    private static int get_num_instruments(byte[] s3m_file) {
        int num_instruments = ScreamTracker3.unsigned_short_le(s3m_file, 34);
        return num_instruments;
    }

    private static int get_num_patterns(byte[] s3m_file) {
        int num_patterns = ScreamTracker3.unsigned_short_le(s3m_file, 36);
        return num_patterns;
    }

    private static int get_instrument_offset(byte[] s3m_file, int instrument_idx) {
        int pointer_offset = 96 + ScreamTracker3.get_num_pattern_orders(s3m_file);
        int instrument_offset = ScreamTracker3.unsigned_short_le(s3m_file, pointer_offset + instrument_idx * 2) << 4;
        return instrument_offset;
    }

    private static int get_sample_data_offset(byte[] s3m_file, int instrument_offset) {
        int sample_data_offset = 0;
        if (s3m_file[instrument_offset] == 1) {
            sample_data_offset = (s3m_file[instrument_offset + 13] & 0xFF) << 20;
            sample_data_offset |= ScreamTracker3.unsigned_short_le(s3m_file, instrument_offset + 14) << 4;
        }
        return sample_data_offset;
    }

    private static int get_sample_data_length(byte[] s3m_file, int instrument_offset) {
        int sample_data_length = 0;
        if (s3m_file[instrument_offset] == 1) {
            sample_data_length = ScreamTracker3.unsigned_short_le(s3m_file, instrument_offset + 16);
        }
        return sample_data_length;
    }

    private static int get_pattern_offset(byte[] s3m_file, int pattern_idx) {
        int pointer_offset = 96 + ScreamTracker3.get_num_pattern_orders(s3m_file);
        int pattern_offset = ScreamTracker3.unsigned_short_le(s3m_file, (pointer_offset += ScreamTracker3.get_num_instruments(s3m_file) * 2) + pattern_idx * 2) << 4;
        return pattern_offset;
    }

    private static int get_pattern_length(byte[] s3m_file, int pattern_offset) {
        int pattern_length = ScreamTracker3.unsigned_short_le(s3m_file, pattern_offset);
        return pattern_length;
    }

    private static byte[] read_more(byte[] old_data, int new_length, DataInput data_input) throws IOException {
        byte[] new_data = old_data;
        if (new_length > old_data.length) {
            new_data = new byte[new_length];
            System.arraycopy(old_data, 0, new_data, 0, old_data.length);
            try {
                data_input.readFully(new_data, old_data.length, new_data.length - old_data.length);
            }
            catch (EOFException e) {
                System.out.println("ScreamTracker3: Module has been truncated!");
            }
        }
        return new_data;
    }

    private static int unsigned_short_le(byte[] buffer, int offset) {
        int value = buffer[offset] & 0xFF;
        return value |= (buffer[offset + 1] & 0xFF) << 8;
    }

    private static String ascii_text(byte[] buffer, int offset, int length) {
        String string;
        byte[] string_buffer = new byte[length];
        for (int idx = 0; idx < length; ++idx) {
            int chr = buffer[offset + idx];
            if (chr < 32) {
                chr = 32;
            }
            string_buffer[idx] = (byte)chr;
        }
        try {
            string = new String(string_buffer, 0, length, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            string = "";
        }
        return string;
    }
}

