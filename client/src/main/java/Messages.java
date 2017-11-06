class InitMsg {
    char[]     cards;
    String[]   players;
    char       threadId;
}

class Msg {
    public String text;

}

class SomeRequest {
    public String text;
}

class SomeResponse {
    public String text;
}

class GameCommand {
    public char  meta;
    public char data[];
}