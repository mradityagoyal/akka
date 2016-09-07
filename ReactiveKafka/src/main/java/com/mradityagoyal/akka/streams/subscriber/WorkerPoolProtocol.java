package com.mradityagoyal.akka.streams.subscriber;

public class WorkerPoolProtocol {

    public static class Msg {

        public final String msgText;

        public Msg(String msgText) {
            this.msgText = msgText;
        }

        @Override
        public String toString() {
            return String.format("Msg(%s, )", msgText);
        }
    }

    public static Msg msg(String msgText) {
        return new Msg(msgText);
    }

    public static class Work {

        public final String msgText;

        public Work(String msgText) {
            this.msgText = msgText;
        }

        @Override
        public String toString() {
            return String.format("Work(%s)", msgText);
        }
    }

    public static Work work(String msgText) {
        return new Work(msgText);
    }

    public static class Reply {

        public final String msgText;

        public Reply(String id) {
            this.msgText = id;
        }

        @Override
        public String toString() {
            return String.format("Reply(%s)", msgText);
        }
    }

    public static Reply reply(String msgText) {
        return new Reply(msgText);
    }

    public static class Done {

        public final String msgText;

        public Done(String msgText) {
            this.msgText = msgText;
        }

        @Override
        public String toString() {
            return String.format("Done(%s)", msgText);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Done done = (Done) o;

            if (msgText != done.msgText) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return msgText.hashCode();
        }

    }

    public static Done done(String msgText) {
        return new Done(msgText);
    }

}
