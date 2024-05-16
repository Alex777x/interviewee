package pl.aliaksandrou.interviewee.audioprocessor.windows;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

import java.lang.reflect.InvocationTargetException;

public class COMUtils {

    public static <T extends IUnknown> T queryInterface(IUnknown unknown, Class<T> comInterfaceClass) {
        Guid.IID iid = getIID(comInterfaceClass);
        PointerByReference pbr = new PointerByReference();

        HRESULT hr = unknown.QueryInterface(new Guid.REFIID(iid), pbr);
        if (FAILED(hr)) {
            throw new RuntimeException("QueryInterface failed: " + hr.intValue());
        }

        try {
            return comInterfaceClass.cast(comInterfaceClass.getConstructor(Pointer.class).newInstance(pbr.getValue()));
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Guid.IID getIID(Class<?> comInterfaceClass) {
        if (comInterfaceClass == CoreAudioUtil.IMMDeviceEnumerator.class) {
            return new Guid.IID(CoreAudioUtil.Mmdeviceapi.IID_IMMDeviceEnumerator.toGuidString());
        } else if (comInterfaceClass == CoreAudioUtil.IMMDevice.class) {
            return new Guid.IID(CoreAudioUtil.Mmdeviceapi.IID_IMMDevice.toGuidString());
        } else if (comInterfaceClass == CoreAudioUtil.IAudioClient.class) {
            return new Guid.IID(CoreAudioUtil.Mmdeviceapi.IID_IAudioClient.toGuidString());
        } else if (comInterfaceClass == CoreAudioUtil.IAudioCaptureClient.class) {
            return new Guid.IID(CoreAudioUtil.Mmdeviceapi.IID_IAudioCaptureClient.toGuidString());
        } else {
            throw new IllegalArgumentException("Unsupported COM interface: " + comInterfaceClass.getName());
        }
    }

    public static boolean FAILED(HRESULT hr) {
        return hr.intValue() < 0;
    }
}
