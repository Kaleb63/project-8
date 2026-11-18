import static org.junit.Assert.assertEquals;

import org.junit.Test;

import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.statement.Statement;
import components.utilities.Tokenizer;

/**
 * JUnit test fixture for {@code Statement}'s {@code parse} and
 * {@code parseBlock} methods.
 *
 * @author Andrew Pavel
 * @author Kaleb Agbobli
 *
 */
public abstract class StatementTest {

    /**
     * The name of a file containing a valid BL statement.
     */
    private static final String FILE_NAME_1 = "data/statement1.bl";

    /**
     * The name of a file containing an invalid BL statement.
     */
    private static final String FILE_NAME_2 = "data/statement2.bl";

    /**
     * Invokes the {@code Statement} constructor for the implementation under
     * test and returns the result.
     *
     * @return the new statement
     * @ensures constructorTest = compose((BLOCK, ?, ?), <>)
     */
    protected abstract Statement constructorTest();

    /**
     * Invokes the {@code Statement} constructor for the reference
     * implementation and returns the result.
     *
     * @return the new statement
     * @ensures constructorRef = compose((BLOCK, ?, ?), <>)
     */
    protected abstract Statement constructorRef();

    /**
     * Test of parse on syntactically valid input.
     */
    @Test
    public final void testParseValidExample() {
        /*
         * Setup
         */
        Statement sRef = this.constructorRef();
        SimpleReader file = new SimpleReader1L(FILE_NAME_1);
        Queue<String> tokens = Tokenizer.tokens(file);
        sRef.parse(tokens);
        file.close();
        Statement sTest = this.constructorTest();
        file = new SimpleReader1L(FILE_NAME_1);
        tokens = Tokenizer.tokens(file);
        file.close();
        /*
         * The call
         */
        sTest.parse(tokens);
        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
    }

    /**
     * Test of parse on syntactically invalid input.
     */
    @Test(expected = RuntimeException.class)
    public final void testParseErrorExample() {
        /*
         * Setup
         */
        Statement sTest = this.constructorTest();
        SimpleReader file = new SimpleReader1L(FILE_NAME_2);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        /*
         * The call--should result in an error being caught
         */
        sTest.parse(tokens);
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

    private void assertValidParse(String... sourceTokens) {
        Statement sRef = this.constructorRef();
        sRef.parse(this.createTokens(sourceTokens));
        Statement sTest = this.constructorTest();
        sTest.parse(this.createTokens(sourceTokens));
        assertEquals(sRef, sTest);
    }

    private void assertValidParseBlock(String... sourceTokens) {
        Statement sRef = this.constructorRef();
        sRef.parseBlock(this.createTokens(sourceTokens));
        Statement sTest = this.constructorTest();
        sTest.parseBlock(this.createTokens(sourceTokens));
        assertEquals(sRef, sTest);
    }

    @Test
    public final void testParseValidCall() {
        this.assertValidParse("move");
    }

    @Test
    public final void testParseValidIf() {
        this.assertValidParse("IF", "true", "THEN", "move", "END", "IF");
    }

    @Test
    public final void testParseValidIfElse() {
        this.assertValidParse("IF", "true", "THEN", "move", "ELSE", "turnleft", "END",
                "IF");
    }

    @Test
    public final void testParseValidWhile() {
        this.assertValidParse("WHILE", "true", "DO", "move", "END", "WHILE");
    }

    @Test
    public final void testParseValidNestedStatements() {
        this.assertValidParse("IF", "true", "THEN", "WHILE", "random", "DO", "move",
                "END", "WHILE", "END", "IF");
    }

    @Test
    public final void testParseBlockValidEmpty() {
        this.assertValidParseBlock();
    }

    @Test
    public final void testParseBlockValidMultipleCalls() {
        this.assertValidParseBlock("move", "turnleft", "infect", "skip");
    }

    @Test
    public final void testParseBlockValidMixedStatements() {
        this.assertValidParseBlock("move", "IF", "true", "THEN", "turnleft", "END", "IF",
                "WHILE", "true", "DO", "skip", "END", "WHILE");
    }

    @Test(expected = RuntimeException.class)
    public final void testParseInvalidToken() {
        this.constructorTest().parse(this.createTokens("PROGRAM"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseIfMissingThen() {
        this.constructorTest()
                .parse(this.createTokens("IF", "true", "move", "END", "IF"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseIfMissingEnd() {
        this.constructorTest().parse(this.createTokens("IF", "true", "THEN", "move"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseWhileMissingDo() {
        this.constructorTest()
                .parse(this.createTokens("WHILE", "true", "move", "END", "WHILE"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseWhileMissingEnd() {
        this.constructorTest().parse(this.createTokens("WHILE", "true", "DO", "move"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseBlockInvalidIf() {
        this.constructorTest()
                .parseBlock(this.createTokens("IF", "true", "THEN", "move", "END"));
    }

    @Test(expected = RuntimeException.class)
    public final void testParseBlockInvalidWhile() {
        this.constructorTest()
                .parseBlock(this.createTokens("WHILE", "true", "DO", "move", "END"));
    }

}
