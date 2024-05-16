package pl.aliaksandrou.interviewee.audioprocessor.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.util.Arrays;
import java.util.List;

public class CoreAudioUtil {

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        void Sleep(int dwMilliseconds);
    }

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

        int MessageBoxA(WinDef.HWND hWnd, String lpText, String lpCaption, int uType);
    }

    public interface Ole32 extends StdCallLibrary {
        Ole32 INSTANCE = Native.load("ole32", Ole32.class);

        int COINIT_MULTITHREADED = 0x0;

        int CoInitializeEx(Pointer pvReserved, int dwCoInit);

        void CoUninitialize();
    }

    public interface Mmdeviceapi extends StdCallLibrary {
        Mmdeviceapi INSTANCE = Native.load("mmdeviceapi", Mmdeviceapi.class);

        HRESULT CoCreateInstance(GUID rclsid, Pointer pUnkOuter, int dwClsContext, GUID riid, PointerByReference ppv);

        class GUID extends Structure {
            public int Data1;
            public short Data2;
            public short Data3;
            public byte[] Data4 = new byte[8];

            public GUID() {
            }

            public GUID(String guid) {
                String[] parts = guid.split("-");
                Data1 = (int) Long.parseLong(parts[0], 16);
                Data2 = (short) Integer.parseInt(parts[1], 16);
                Data3 = (short) Integer.parseInt(parts[2], 16);
                String data4 = parts[3] + parts[4];
                for (int i = 0; i < 8; i++) {
                    Data4[i] = (byte) Integer.parseInt(data4.substring(i * 2, i * 2 + 2), 16);
                }
            }

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("Data1", "Data2", "Data3", "Data4");
            }

            public String toGuidString() {
                return String.format("%08x-%04x-%04x-%02x%02x-%012x",
                        Data1, Data2, Data3, Data4[0], Data4[1], new String(Data4, 2, 6));
            }
        }

        GUID CLSID_MMDeviceEnumerator = new GUID("BCDE0395-E52F-467C-8E3D-C4579291692E");
        GUID IID_IMMDeviceEnumerator = new GUID("A95664D2-9614-4F35-A746-DE8DB63617E6");
        GUID IID_IMMDevice = new GUID("D666063F-1587-4E43-81F1-B948E807363F");
        GUID IID_IAudioClient = new GUID("1CB9AD4C-DBFA-4c32-B178-C2F568A703B2");
        GUID IID_IAudioCaptureClient = new GUID("C8ADBD64-E71E-48a0-A4DE-185C395CD317");
    }

    public interface IMMDeviceEnumerator extends IUnknown {
        HRESULT GetDefaultAudioEndpoint(int dataFlow, int role, PointerByReference ppDevice);
    }

    public interface IMMDevice extends IUnknown {
        HRESULT Activate(Guid.REFIID iid, int dwClsCtx, Pointer pActivationParams, PointerByReference ppInterface);
    }

    public interface IAudioClient extends IUnknown {
        HRESULT GetMixFormat(PointerByReference ppDeviceFormat);

        HRESULT Initialize(int shareMode, int streamFlags, long hnsBufferDuration, long hnsPeriodicity, Pointer pFormat, Pointer pAudioSessionGuid);

        HRESULT GetBufferSize(IntByReference pNumBufferFrames);

        HRESULT GetService(Guid.REFIID riid, PointerByReference ppv);

        HRESULT Start();

        HRESULT Stop();
    }

    public interface IAudioCaptureClient extends IUnknown {
        HRESULT GetBuffer(PointerByReference ppData, IntByReference pNumFramesToRead, IntByReference pdwFlags, LongByReference pu64DevicePosition, LongByReference pu64QPCPosition);

        HRESULT ReleaseBuffer(int NumFramesRead);
    }
}
