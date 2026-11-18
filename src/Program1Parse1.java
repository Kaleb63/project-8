import components.map.Map;
import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.statement.Statement1;
import components.utilities.Reporter;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Kaleb Agbobli
 * @author Andrew Pavel
 *
 */
public final class Program1Parse1 extends Program1 {

    /**
     * Parses one BL instruction definition.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     */
    private static String parseInstruction(Queue<String> tokens, Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION")
                : "Violation of: INSTRUCTION is a prefix of tokens";

        tokens.dequeue(); // INSTRUCTION

        Reporter.assertElseFatalError(
                tokens.length() > 0 && Tokenizer.isIdentifier(tokens.front()),
                "Expected instruction name");
        String name = tokens.dequeue();

        String lowerName = name.toLowerCase();
        Reporter.assertElseFatalError(
                !lowerName.equals("move") && !lowerName.equals("turnleft")
                        && !lowerName.equals("turnright") && !lowerName.equals("infect")
                        && !lowerName.equals("skip"),
                "Instruction name cannot be primitive");

        Reporter.assertElseFatalError(tokens.length() > 0 && tokens.front().equals("IS"),
                "Expected IS");
        tokens.dequeue(); // IS

        Statement parsedBody = new Statement1();
        parsedBody.parseBlock(tokens);
        body.transferFrom(parsedBody);

        Reporter.assertElseFatalError(tokens.length() > 0 && tokens.front().equals("END"),
                "Expected END");
        tokens.dequeue(); // END

        Reporter.assertElseFatalError(
                tokens.length() > 0 && Tokenizer.isIdentifier(tokens.front()),
                "Expected closing instruction name");
        String closingName = tokens.dequeue();

        Reporter.assertElseFatalError(closingName.equals(name),
                "Instruction names do not match");

        return name;
    }

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";

        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";

        Reporter.assertElseFatalError(
                tokens.length() > 0 && tokens.front().equals("PROGRAM"),
                "Expected PROGRAM");
        tokens.dequeue(); // PROGRAM

        Reporter.assertElseFatalError(
                tokens.length() > 0 && Tokenizer.isIdentifier(tokens.front()),
                "Expected program name");
        String programName = tokens.dequeue();

        Reporter.assertElseFatalError(tokens.length() > 0 && tokens.front().equals("IS"),
                "Expected IS");
        tokens.dequeue(); // IS

        Program parsed = new Program1();
        parsed.setName(programName);
        Map<String, Statement> context = parsed.newContext();

        while (tokens.length() > 0 && tokens.front().equals("INSTRUCTION")) {
            Statement body = new Statement1();
            String instructionName = parseInstruction(tokens, body);

            Reporter.assertElseFatalError(!context.hasKey(instructionName),
                    "Duplicate instruction name");
            context.add(instructionName, body);
        }

        parsed.swapContext(context);

        Reporter.assertElseFatalError(
                tokens.length() > 0 && tokens.front().equals("BEGIN"), "Expected BEGIN");
        tokens.dequeue(); // BEGIN

        Statement mainBody = new Statement1();
        mainBody.parseBlock(tokens);
        parsed.swapBody(mainBody);

        Reporter.assertElseFatalError(tokens.length() > 0 && tokens.front().equals("END"),
                "Expected END");
        tokens.dequeue(); // END

        Reporter.assertElseFatalError(
                tokens.length() > 0 && Tokenizer.isIdentifier(tokens.front()),
                "Expected closing program name");
        String closingName = tokens.dequeue();

        Reporter.assertElseFatalError(closingName.equals(programName),
                "Program names do not match");

        Reporter.assertElseFatalError(
                tokens.length() > 0 && tokens.front().equals(Tokenizer.END_OF_INPUT),
                "Unexpected extra input");
        tokens.dequeue(); // END_OF_INPUT

        this.transferFrom(parsed);
    }

    /**
     * Main method.
     *
     * @param args
     *            command-line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();

        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);

        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }
}
