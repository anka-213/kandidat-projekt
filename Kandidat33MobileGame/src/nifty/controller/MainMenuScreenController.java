package nifty.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author forssenm
 */
public class MainMenuScreenController implements ScreenController {

    private Nifty nifty;
    private Element image;

    @Override
    public void bind(final Nifty newNifty, final Screen newScreen) {
        this.nifty = newNifty;
        System.out.println("Startar screenen");
    }

    @Override
    public void onStartScreen() {   
    }

    @Override
    public void onEndScreen() {
        System.out.println("Ending MenuScreen");
    }
}
