package team.hiddenark.stickmangame.mac;


import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;

public class JNAUtil {
    public interface Quartz extends Library {
        Quartz INSTANCE = Native.load("ApplicationServices", Quartz.class);

        // Function to get window information
        CoreFoundation.CFDictionaryRef CGWindowListCopyWindowInfo(int option, int relativeToWindow);

        // Option constants
        int kCGWindowListOptionAll = 0;
        int kCGWindowListExcludeDesktopElements = 0x10;
    }


    public static void printDictionary(CoreFoundation.CFDictionaryRef dictionary) {
        Pointer dictPointer = dictionary.getPointer();

        int count = CoreFoundationLibrary.INSTANCE.CFDictionaryGetCount(dictPointer);

        Pointer[] keys = new Pointer[count];
        Pointer[] values = new Pointer[count];

        CoreFoundationLibrary.INSTANCE.CFDictionaryGetKeysAndValues(dictPointer, keys, values);

        for (int i = 0; i < count; i++) {
            String key = convertCFStringRefToString(keys[i]);
            Pointer valuePointer = values[i];
            long typeID = CoreFoundationLibrary.INSTANCE.CFGetTypeID(valuePointer);

            String value;

//            if (typeID == CoreFoundationLibrary.INSTANCE.CFStringGetTypeID()) {
//                value = convertCFStringRefToString(valuePointer);
//            } else if (typeID == CoreFoundationLibrary.INSTANCE.CFNumberGetTypeID()) {
//                value = convertCFNumberToString(valuePointer);
//            } else if (typeID == CoreFoundationLibrary.INSTANCE.CFBooleanGetTypeID()) {
//                value = valuePointer.equals(CoreFoundationConstants) ? "true" : "false";
//            } else if (typeID == CoreFoundationLibrary.INSTANCE.CFDictionaryGetTypeID()) {
//                value = convertCFDictionaryToString(valuePointer);
//            } else {
//                value = "Unsupported type: " + typeID;
//            }

//            System.out.println("Key: " + key + ", Value: " + value);
        }
    }


    public static String convertCFDictionaryToString(Pointer dictionaryPointer) {
        if (dictionaryPointer == null) {
            return "null";
        }

        CoreFoundation.CFDictionaryRef dictionary = new CoreFoundation.CFDictionaryRef(dictionaryPointer);

        // Capture the dictionary's key-value pairs
        StringBuilder builder = new StringBuilder("{ ");
        printDictionary(dictionary); // Traverse and print the dictionary
        builder.append(" }");

        return builder.toString();
    }



    public static String convertCFStringRefToString(Pointer cfStringPointer) {
        if (cfStringPointer == null) {
            return null;
        }

        // Try to get a direct C string pointer
        Pointer cStringPointer = CoreFoundationLibrary.INSTANCE.CFStringGetCStringPtr(
                cfStringPointer, CoreFoundationLibrary.kCFStringEncodingUTF8
        );

        if (cStringPointer != null) {
            return cStringPointer.getString(0, "UTF-8");
        }

        // Fall back to manually extracting the string into a buffer
        byte[] buffer = new byte[1024];
        boolean success = CoreFoundationLibrary.INSTANCE.CFStringGetCString(
                cfStringPointer, buffer, buffer.length, CoreFoundationLibrary.kCFStringEncodingUTF8
        ) != 0;

        if (success) {
            return new String(buffer, 0, buffer.length, java.nio.charset.StandardCharsets.UTF_8).trim();
        }

        return "Unknown String";
    }

    public static String convertCFNumberToString(Pointer numberPointer) {
        if (numberPointer == null) {
            return "null";
        }

        // Attempt to interpret as an integer
        Pointer intValue = new Memory(Integer.BYTES);
        boolean isInt = CoreFoundationLibrary.INSTANCE.CFNumberGetValue(numberPointer,
                CoreFoundationLibrary.kCFNumberIntType, intValue);

        if (isInt) {
            return String.valueOf(intValue.getInt(0));
        }

        // Fallback: Interpret as a double
        Pointer doubleValue = new Memory(Double.BYTES);
        boolean isDouble = CoreFoundationLibrary.INSTANCE.CFNumberGetValue(numberPointer,
                CoreFoundationLibrary.kCFNumberFloat64Type, doubleValue);

        if (isDouble) {
            return String.valueOf(doubleValue.getDouble(0));
        }

        return "Unknown CFNumber";
    }


    class CoreFoundationConstants {
        public final Pointer kCFBooleanTrue = new Pointer(0x7ff80001); // Pointer for true
        public final Pointer kCFBooleanFalse = new Pointer(0x7ff80000); // Pointer for false
    }
    interface CoreFoundationLibrary extends Library {
        CoreFoundationLibrary INSTANCE = Native.load("CoreFoundation", CoreFoundationLibrary.class);

        boolean CFNumberGetValue(Pointer number, int type, Pointer value);

        int kCFNumberIntType = 9;  // CFNumberIntType for integers
        int kCFNumberFloat64Type = 18; // CFNumberFloat64Type for doubles

        long CFDictionaryGetTypeID();


        int CFStringGetCString(Pointer cfString, byte[] buffer, long bufferSize, int encoding);

        Pointer CFStringGetCStringPtr(Pointer cfString, int encoding);

        // Encoding constants
        int kCFStringEncodingUTF8 = 0x08000100;

        // Get the count of items in a dictionary
        int CFDictionaryGetCount(Pointer dictionary);

        // Retrieve keys and values from the dictionary
        void CFDictionaryGetKeysAndValues(Pointer dictionary, Pointer[] keys, Pointer[] values);

        // Get the type ID of a CoreFoundation object
        long CFGetTypeID(Pointer cfObject);

        // Type identifiers for common CoreFoundation types
        long CFStringGetTypeID();
        long CFNumberGetTypeID();
        long CFBooleanGetTypeID();
    }
}
