package kr.merutilm.pacl.data;


import javax.annotation.Nonnull;

import kr.merutilm.base.util.ConsoleUtils;

import java.io.File;

/**
 * The type Level editor.
 */
public final class PACL {
    private Process processType;
    private File inputFile;
    private File outputFile;
    private CustomLevel customLevel;

    @Nonnull
    public CustomLevel level() {
        return customLevel;
    }

    int processCurrent = 0;
    int processAll = 1;

    public Process getProcessType() {
        return processType;
    }

    public void setProcessType(Process processType) {
        this.processType = processType;
    }

    public double getParsingProgress() {
        if (processType == Process.FINISHED) {
            return 100;
        }
        return (double) processCurrent / processAll * 100;
    }

    public void setCustomLevel(@Nonnull CustomLevel level) {
        this.customLevel = level;
    }

    private PACL(CustomLevel customLevel) {
        Thread currentRenderThread;
        currentRenderThread = new Thread(() -> new CSMainFrame(this));
        try {
            this.customLevel = customLevel;
            processType = Process.LOAD;
            currentRenderThread.start();
        } catch (RuntimeException e) {
            ConsoleUtils.logError(e);
            processType = Process.ERROR;
        }
    }

    public static void main(String[] args) {
        new PACL(CustomLevel.createLevel());
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
        this.outputFile = null;
    }

    public File getInputFile() {
        return inputFile;
    }

    /**
     * 파일 출력 경로 정의 <p>
     * 기본값 : 파일 입력 경로
     */
    public String generateOutputFolder() {
        outputFile = inputFile == null ? null : inputFile.getParentFile();
        return outputFile == null ? null : outputFile.getAbsolutePath();
    }

}


























