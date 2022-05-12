public class ClassExperiment {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            ClassThread classThread = new ClassThread();
            classThread.start();
            Thread.sleep(5000);
        }
    }
}
