package system;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

public class OS {
    private static final int MAX_TITLE_LENGTH = 1024;
    protected static final String OperatingSystem = System.getProperty("os.name");

    public static String getOperatingSystem() {
        return OperatingSystem;
    }

    public static void jna() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        System.out.println("Active window title: " + Native.toString(buffer));
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, rect);
        System.out.println("rect = " + rect);
    }

    protected static String getImageName(WinDef.HWND window) {
        // Get the process ID of the window
        IntByReference procId = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(window, procId);
        // Open the process to get permissions to the image name
        WinNT.HANDLE procHandle = Kernel32.INSTANCE.OpenProcess(
                Kernel32.PROCESS_QUERY_LIMITED_INFORMATION,
                false,
                procId.getValue()
        );
        // Get the image name
        char[] buffer = new char[4096];
        IntByReference bufferSize = new IntByReference(buffer.length);
        boolean success = Kernel32.INSTANCE.QueryFullProcessImageName(procHandle, 0, buffer, bufferSize);
        // Clean up: close the opened process
        Kernel32.INSTANCE.CloseHandle(procHandle);
        return success ? new String(buffer, 0, bufferSize.getValue()) : null;
    }

    public static boolean isWindows() {
        return getOperatingSystem().equals("Windows 10") || getOperatingSystem().equals("Windows 7");
    }

    public static boolean isLinux() {
        return getOperatingSystem().equals("Linux");
    }
}
