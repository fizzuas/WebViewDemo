package com.kydw.webviewdemo.util.shellutil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * ShellUtils
 * <ul>
 * <strong>Check root</strong>
 * <li>{@link ShellUtils#checkRootPermission()}</li>
 * </ul>
 * <ul>
 * <strong>Execte command</strong>
 * <li>{@link ShellUtils#execCommand(String, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String, boolean, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(List, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(List, boolean, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String[], boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String[], boolean, boolean)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
 */
public class ShellUtils {

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    private ShellUtils() {
        throw new AssertionError();
    }

    /**
     * check whether has root permission
     *
     * @return
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    /**
     * execute shell command, default return result msg
     *
     * @param command command
     * @param isRoot  whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     *
     * @param commands command list
     * @param isRoot   whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     *
     * @param commands command array
     * @param isRoot   whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    /**
     * execute shell command
     *
     * @param command         command
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands        command list
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands        command array
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return <ul>
     * <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and
     * {@link CommandResult#errorMsg} is null.</li>
     * <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
     * </ul>
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s + "\n");
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }

    /**
     * result of command
     * <ul>
     * <li>{@link CommandResult#result} means result of command, 0 means normal, else means error, same to excute in
     * linux shell</li>
     * <li>{@link CommandResult#successMsg} means success message of command result</li>
     * <li>{@link CommandResult#errorMsg} means error message of command result</li>
     * </ul>
     *
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
     */
    public static class CommandResult {

        @Override
        public String toString() {
            return "CommandResult{" +
                    "result=" + result +
                    ", successMsg='" + successMsg + '\'' +
                    ", errorMsg='" + errorMsg + '\'' +
                    '}';
        }

        /**
         * result of command
         **/
        public int result;
        /**
         * success message of command result
         **/
        public String successMsg;
        /**
         * error message of command result
         **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

    }

    public static CommandResult execClick(int x, int y) {
        //(303,353)
        String cmd1 = "sendevent /dev/input/event5 0001 330 00000001";// BTN touch事件 值为1

        String cmd2 = " sendevent /dev/input/event5 0003 58  00000001";//Pressure on contact area

        String cmd3 = " sendevent /dev/input/event5 0003 0053 " + y;
        String cmd4 = " sendevent /dev/input/event5 0003 0054 " + x;

        String cmd5 = " sendevent /dev/input/event5 0000 0002 00000000";

        String cmd6 = " sendevent /dev/input/event5 0000 0000 00000000"; //同步

        //-------------------

        String cmd7 = " sendevent /dev/input/event5 0001 0330 00000000"; // BTN touch事件 值为0

        String cmd8 = " sendevent /dev/input/event5 0003 0058 00000000"; //Pressure on contact area

        String cmd9 = " sendevent /dev/input/event5 0003 0053 " + y;
        String cmd10 = " sendevent /dev/input/event5 0003 0054 " + x;

        String cmd11 = " sendevent /dev/input/event5 0000 0002 00000000";
        String cmd12 = "sendevent /dev/input/event5 0000 0000 00000000";   //同步

        String[] cmds = new String[]{cmd1, cmd2, cmd3, cmd4, cmd5, cmd6, cmd7, cmd8, cmd9, cmd10, cmd11, cmd12};


        return execCommand(cmds, true);


    }

    public static CommandResult execClick2(int x, int y) {
        //(303,353)
        String cmd1 = "sendevent /dev/input/event5 0001 330 00000001";// BTN touch事件 值为1


        String cmd2 = "sendevent /dev/input/event5 0003 0053 " + y;
        String cmd3 = "sendevent /dev/input/event5 0003 0054 " + x;

        String cmd4 = "sendevent /dev/input/event5 0000 0002 00000000";
        String cmd5 = "sendevent /dev/input/event5 0000 0000 00000000";//同步

        String cmd6 = "sendevent /dev/input/event5 0000 0002 00000000";
        String cmd7 = "sendevent /dev/input/event5 0000 0000 00000000";//同步

        String cmd8 = "sendevent /dev/input/event5 0001 0330 00000000";// BTN touch事件 值为0

        String cmd9 = "sendevent /dev/input/event5 0003 0053 00000000";
        String cmd10 = "sendevent /dev/input/event5 0003 0054 00000900";

        String cmd11 = "sendevent /dev/input/event5 0000 0002 00000000";
        String cmd12 = "sendevent /dev/input/event5 0000 0000 00000000";   //同步

        String[] cmds = new String[]{cmd1, cmd2, cmd3, cmd4, cmd5, cmd6, cmd7, cmd8, cmd9, cmd10, cmd11, cmd12};


        return execCommand(cmds, true);
    }


}
