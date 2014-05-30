import com.leapmotion.leap.*
import com.leapmotion.leap.Gesture.State
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment

class MousePointerListener extends Listener {

    Robot robot = new Robot()
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    int width = gd.getDisplayMode().getWidth();
    int height = gd.getDisplayMode().getHeight();

    public void onInit(Controller controller) {
        println "Initialized"
    }

    public void onConnect(Controller controller) {
        println "Connected"
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        println "Disconnected"
    }

    public void onExit(Controller controller) {
        println "Exited"
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        
        if (!frame.hands().isEmpty()) {
            // Get the first hand
            Hand hand = frame.hands().get(0);

            // Check if the hand has any fingers
            FingerList fingers = hand.fingers();
            if (!fingers.isEmpty()) {
                // Calculate the hand's average finger tip position
                Vector avgPos = Vector.zero();
                for (Finger finger : fingers) {
                    avgPos = avgPos.plus(finger.tipPosition());
                }
                avgPos = avgPos.divide(fingers.count());
                //println "Hand has " + fingers.count() + " fingers, average finger tip position: " + avgPos
                robot.mouseMove((avgPos.x/150*width+width/2) as int, (height*1.2-avgPos.y/150*height) as int)
            }

        }

        GestureList gestures = frame.gestures();
        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);

            switch (gesture.type()) {
                case Gesture.Type.TYPE_SCREEN_TAP:
                    ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
                    System.out.println("Screen Tap id: " + screenTap.id()
                               + ", " + screenTap.state()
                               + ", position: " + screenTap.position()
                               + ", direction: " + screenTap.direction());
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    break;
                case Gesture.Type.TYPE_KEY_TAP:
                    KeyTapGesture keyTap = new KeyTapGesture(gesture);
                    System.out.println("Key Tap id: " + keyTap.id()
                               + ", " + keyTap.state()
                               + ", position: " + keyTap.position()
                               + ", direction: " + keyTap.direction());
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    break;
                default:
                    System.out.println("Unknown gesture type.");
                    break;
            }
        }
    }
}

// Create a sample listener and controller
MousePointerListener listener = new MousePointerListener();
Controller controller = new Controller();

// Have the sample listener receive events from the controller
controller.addListener(listener);

// Keep this process running until Enter is pressed
println "Press Enter to quit...";
try {
    System.in.read();
} catch (IOException e) {
    e.printStackTrace();
}

// Remove the sample listener when done
controller.removeListener(listener);
