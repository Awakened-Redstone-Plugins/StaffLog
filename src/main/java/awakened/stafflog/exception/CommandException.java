package awakened.stafflog.exception;

public class CommandException extends Exception {

    /*
     * This code has entirely been copied from HolographicDisplays.
     * The code has been extracted from the GitHub.
     * https://github.com/filoghost/HolographicDisplays
     * http://dev.bukkit.org/bukkit-plugins/holographic-displays
     *
     * Some modifications were made.
     */

    private static final long serialVersionUID = 1L;

    public CommandException(String message) {
        super(message);
    }
}

