package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class InvalidFileException extends Exception {
    private final int lineWithError;

    public InvalidFileException(String message, int lineWithError) {
        super(message);
        this.lineWithError = lineWithError;
    }

    public int getLineWithError() {
        return lineWithError;
    }
}