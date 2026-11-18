import static org.junit.Assert.assertEquals;

import org.junit.Test;

import components.program.Program;
import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.utilities.Tokenizer;

/**
 * JUnit test fixture for {@code Program}'s {@code parse} method.
 *
 * @author Put your name here
 *
 */
public abstract class ProgramTest {

    /**
     * The name of a file containing a valid BL program.
     */
    private static final String FILE_NAME_1 = "data/program1.bl";

    /**
     * The name of a file containing an invalid BL program.
     */
    private static final String FILE_NAME_2 = "data/program2.bl";

    /**
     * Invokes the {@code Program} constructor for the implementation under test
     * and returns the result.
     *
     * @return the new program
     * @ensures constructorTest = ("Unnamed", {}, compose((BLOCK, ?, ?), <>))
     */
    protected abstract Program constructorTest();

    /**
     * Invokes the {@code Program} constructor for the reference implementation
     * and returns the result.
     *
     * @return the new program
     * @ensures constructorRef = ("Unnamed", {}, compose((BLOCK, ?, ?), <>))
     */
    protected abstract Program constructorRef();

    /**
     * Test of parse on syntactically valid input.
     */
    @Test
    public final void testParseValidExample() {
        /*
         * Setup
         */
        Program pRef = this.constructorRef();
        SimpleReader file = new SimpleReader1L(FILE_NAME_1);
        pRef.parse(file);
        file.close();
        Program pTest = this.constructorTest();
        file = new SimpleReader1L(FILE_NAME_1);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        /*
         * The call
         */
        pTest.parse(tokens);
        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test of parse on syntactically invalid input.
     */
    @Test(expected = RuntimeException.class)
    public final void testParseErrorExample() {
        /*
         * Setup
         */
        Program pTest = this.constructorTest();
        SimpleReader file = new SimpleReader1L(FILE_NAME_2);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        /*
         * The call--should result in a syntax error being found
         */
        pTest.parse(tokens);
    }

    /*
     * Tests for project
     */

    private Queue<String> createTokens(String... sourceTokens) {
        Queue<String> tokens = new Queue1L<>();
        for (String token : sourceTokens) {
            tokens.enqueue(token);
        }
        tokens.enqueue(Tokenizer.END_OF_INPUT);
        return tokens;
    }

    private void assertValid(String... sourceTokens) {
        Program pRef = this.constructorRef();
        pRef.parse(this.createTokens(sourceTokens));
        Program pTest = this.constructorTest();
        pTest.parse(this.createTokens(sourceTokens));
        assertEquals(pRef, pTest);
    }

    @Test
    public final void testParseValidNoInstructions() {
        this.assertValid("PROGRAM", "P", "IS", "BEGIN", "move", "END", "P");
    }

    @Test
    public final void testParseValidOneInstruction() {
        this.assertValid("PROGRAM", "P", "IS", "INSTRUCTION", "foo", "IS", "move", "END",
                "foo", "BEGIN", "foo", "END", "P");
    }

    @Test
    public final void testParseValidMultipleInstructions() {
        this.assertValid("PROGRAM", "P", "IS", "INSTRUCTION", "foo", "IS", "move", "END",
                "foo", "INSTRUCTION", "bar", "IS", "skip", "END", "bar", "BEGIN", "foo",
                "bar", "END", "P");
    }

    @Test(expected = RuntimeException.class)
    public final void testParseMissingProgramKeyword() {
        this.constructorTest()
                .parse(this.createTokens("P", "IS", "BEGIN", "move", "END", "P"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseMissingIdentifier() {
        this.constructorTest()
                .parse(this.createTokens("PROGRAM", "IS", "BEGIN", "move", "END", "P"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseMissingIs() {
        this.constructorTest()
                .parse(this.createTokens("PROGRAM", "P", "BEGIN", "move", "END", "P"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseMissingBegin() {
        this.constructorTest()
                .parse(this.createTokens("PROGRAM", "P", "IS", "move", "END", "P"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseMismatchedProgramNames() {
        this.constructorTest().parse(
                this.createTokens("PROGRAM", "P", "IS", "BEGIN", "move", "END", "Q"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseDuplicateInstructionNames() {
        this.constructorTest()
                .parse(this.createTokens("PROGRAM", "P", "IS", "INSTRUCTION", "foo", "IS",
                        "move", "END", "foo", "INSTRUCTION", "foo", "IS", "skip", "END",
                        "foo", "BEGIN", "foo", "END", "P"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseMismatchedInstructionNames() {
        this.constructorTest()
                .parse(this.createTokens("PROGRAM", "P", "IS", "INSTRUCTION", "foo", "IS",
                        "move", "END", "bar", "BEGIN", "move", "END", "P"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseUnexpectedTrailingInput() {
        this.constructorTest().parse(this.createTokens("PROGRAM", "P", "IS", "BEGIN",
                "move", "END", "P", "extra"));
    }

}
