package team.hiddenark.stickmangame.window;

import com.sun.jna.Native;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import team.hiddenark.stickmangame.GameWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class WindowHandleList extends ArrayList<WindowHandle> {
    private final String[] ignore;
    private final GameWindow mainWindow;

    public WindowHandleList(GameWindow mainWindow){
        this.mainWindow = mainWindow;
        ignore = new String[]{"Movies1 & TV", "Windows Input Experience", "Program Manager", "Setup", "Recording toolbar", mainWindow.getTitle()};
    }

    public boolean contains(WinDef.HWND window){
        for (WindowHandle w : this){
            if (w.getHWND().equals(window)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(WindowHandle windowHandle) {
        if (contains(windowHandle.getHWND())) return false;
        mainWindow.addObject(windowHandle);
        return super.add(windowHandle);
    }

    public boolean add(WinDef.HWND window) {
        if (contains(window)) return false;
        WindowHandle windowHandle = new WindowHandle(mainWindow, window);
        mainWindow.addObject(windowHandle);
        return super.add(windowHandle);
    }

    public void addOpenWindows(boolean includeAll){
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            if (WindowHandle.isWindow(hWnd) && (WindowHandle.isVisible(hWnd) || includeAll)) {
                String title = WindowUtils.getWindowTitle(hWnd);
                if ((!title.isEmpty() && !Arrays.asList(ignore).stream().anyMatch(title::contains)) || includeAll){
                    if (!this.contains(hWnd)){
                        this.add(hWnd);
                    }
                }
            }
            return true; // Continue enumeration
        }, null);
    }

    private WinNT.HANDLE hHook;
    private Thread thread;

    public void listenForNewWindows(){

        WinUser.WinEventProc eventProc = (handle, event, hWnd, idObject, idChild, dwEventThread, dwmsEventTime) -> {
            int eventCode = event.intValue();
            int objectId = idObject.intValue();
            String title = WindowUtils.getWindowTitle(hWnd);
            char[] chars = new char[100];
            User32.INSTANCE.GetClassName(hWnd,chars,100);
            mainWindow.runAddAll();

//            add(hWnd);
//            if (eventCode == 32768 && objectId == -8) {
//                if (WindowHandle.isWindow(hWnd) && (WindowHandle.isVisable(hWnd))){
//
//                    if (!title.isEmpty() && Arrays.stream(ignore).noneMatch(title::contains)){
//
//                    }
//                }
//            }
        };

        thread = new Thread(() -> {
            // Force creation of a message queue
            WinUser.MSG msg = new WinUser.MSG();
            User32.INSTANCE.PeekMessage(msg, null, 0, 0, 0);

            // Set the hook after the queue is ready
            hHook = User32.INSTANCE.SetWinEventHook(
                    0x8000, 0x8000, null, eventProc, 0, 0,
                    0x0002
            );

            if (hHook == null) {
                System.err.println("Failed to set WinEvent hook. Error: " + Native.getLastError());
                return;
            }

            System.out.println("started");

            // Now start the message loop
            while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
                User32.INSTANCE.TranslateMessage(msg);
                User32.INSTANCE.DispatchMessage(msg);
            }

            User32.INSTANCE.UnhookWinEvent(hHook);
        });
        thread.setDaemon(true);
        thread.start();

    }

    public WindowHandle get(WinDef.HWND window){
        for (WindowHandle w : this){
            if (w.getHWND().equals(window)){
                return w;
            }
        }
        return null;
    }

    public WindowHandle getFocused(){
        WinDef.HWND window = User32.INSTANCE.GetForegroundWindow();
        if (Arrays.asList(ignore).stream().anyMatch(WindowUtils.getWindowTitle(window)::contains)){
            return null;
        }
        WindowHandle windowHandle = get(window);
        if (windowHandle == null) {
            windowHandle = new WindowHandle(mainWindow, window);
            add(windowHandle);
        }
        return windowHandle;
    }

    public void stopListeningForNewWindows(){
        User32.INSTANCE.UnhookWinEvent(hHook);
        thread.interrupt();
    }

    public boolean isWindowValid(WinDef.HWND hwnd){
        if (WindowHandle.isWindow(hwnd) && WindowHandle.isVisible(hwnd)) {
            String title = WindowUtils.getWindowTitle(hwnd);
            if (!title.isEmpty() && !Arrays.asList(ignore).stream().anyMatch(title::contains)){
                return true;
            }
        }
        return false;
    }

    public WindowHandle getAndAdd(WinDef.HWND hwnd){
        WindowHandle window = this.get(hwnd);
        System.out.println(window);
        if (window == null){
            window = new WindowHandle(mainWindow, hwnd);
            add(window);
        }
        return window;
    }

    public WindowHandle getTop(){
        AtomicReference<WindowHandle> output = new AtomicReference<>();
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            if (isWindowValid(hWnd)){
                output.set(getAndAdd(hWnd));
                return false;
            }
            return true;
        },null);
        return output.get();
    }


}
