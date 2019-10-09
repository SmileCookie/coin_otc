package com.world.util;

import com.world.constant.Const;
import com.world.data.mysql.Query;
import com.world.model.dao.bill.BillDetailDao;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.statisticalReport.TimerLog;
import com.world.model.statisticalreport.dao.TimerLogDao;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.world.constant.Const.Type_Mysql_Transfer;

public class RunSH {


    private Runtime runtime;
    private BufferedReader br;
    private Process process;

    TimerLogDao timerLogDao = new TimerLogDao();
    BillDetailDao bwDao = new BillDetailDao();

    /**
     * 初始化
     */
    public RunSH() {

        runtime = Runtime.getRuntime();
    }


    /**
     * @param cmd 执行的普通shell 命令
     */

    public void run(String cmd) {

        String inline;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Timestamp yesterday = TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));
            Timestamp today = TimeUtil.getTodayFirst();
            Timestamp todaylast = TimeUtil.getTodayLast();
            List<TimerLog> timerExtractList = timerLogDao.getList(sdf.format(today), sdf.format(todaylast), Const.Mysql_Transfer);

            if (CollectionUtils.isEmpty(timerExtractList)) {
                String begin = TimeUtil.parseDate(System.currentTimeMillis());
                this.process = this.runtime.exec(cmd);
                String end = TimeUtil.parseDate(System.currentTimeMillis());
                br = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
                Query<BillDetails> query = bwDao.getQuery();
                query.setSql("select * from bill");
                query.setCls(BillDetails.class);
                if (yesterday != null) {
                    query.append(" and sendTime>=cast('" + yesterday + "' as datetime)");
                }

                if (today != null) {
                    query.append(" and sendTime<=cast('" + today + "' as datetime)");
                }
                int total = query.count();
                while (null != (inline = br.readLine())) {
                    if (Const.Transer_Success.equals(inline)) {
                        timerLogDao.insert(begin, end, Type_Mysql_Transfer, total);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }

    }

    /**
     * @param cmd 执行sql的shell 命令，
     * @throws Exception 如果命令行中含有 SQL时，抛出异常信息
     */

    public void runSql(String cmd) throws Exception {
        String inline;
        try {
            this.process = this.runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(this.process
                    .getInputStream()));
            while (null != (inline = br.readLine())) {
                System.out.println(inline);
                // 信息以SQL 开头
                if (inline.startsWith("SQL")) {
                    throw new Exception(inline);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }

    }

    /**
     * @param cmd 导入数据的shell命令
     * @return 导入总数和成功数相等时，返回true 否则返回false
     */

    public int runImportCmdInt(String cmd) {
        String inline;
        int allCount = 0;
        String rej = "-1";
        boolean flag = false;
        try {
            this.process = this.runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(this.process
                    .getInputStream()));
            while (null != (inline = br.readLine())) {

                if (inline.startsWith("Number of rows read         =")) {
                    allCount = Integer.parseInt(inline.substring(inline
                            .indexOf("=") + 2));
                    flag = true;
                }
                if (inline.startsWith("Number of rows rejected     =")) {
                    rej = inline.substring(inline.indexOf("=") + 2);
                }
                System.out.println(inline);
            }

            if (!flag) {
                allCount = -1;
            }

            if (!"0".equals(rej)) {
                allCount = -1;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return -1;
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }
        return allCount;

    }

    /**
     * @param cmd 导入数据的shell命令
     * @return 导入总数和成功数相等时，返回true 否则返回false
     */

    public boolean runImpotCmd(String cmd) {
        String inline;
        String allCount = "0";
        String rej = "0";
        boolean flag = true;

        try {
            this.process = this.runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(this.process
                    .getInputStream()));
            while (null != (inline = br.readLine())) {

                if (inline.startsWith("Number of rows read         =")) {
                    allCount = inline.substring(inline.indexOf("=") + 2);
                }

                if (inline.startsWith("Number of rows rejected     =")) {
                    rej = inline.substring(inline.indexOf("=") + 2);
                }
                System.out.println(inline);

            }
            if (!"0".equals(rej)) {
                flag = false;
            } else {
                System.out.println("导入*****\t"
                        + cmd.substring(cmd.indexOf("into") + 5) + "\t"
                        + allCount + "\t*****");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }
        return flag;

    }

    /**
     * @param cmd 执行数据导出的shell命令
     * @return 返回导出数据的行数
     */
    public String runExportCmd(String cmd) {
        String inline;
        String rows = "99999";
        boolean flag = false;
        try {
            this.process = this.runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(this.process
                    .getInputStream()));
            while (null != (inline = br.readLine())) {
                if (inline.startsWith("Number of rows")) {
                    flag = true;
                    rows = inline.substring(inline.indexOf(":") + 2).trim();
                    System.out.println("导出*****\t" + rows + "\t*****");
                    break;
                }
            }

        } catch (Exception e) {
            // e.printStackTrace();
            return "-1";
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }
        if (!flag) {
            rows = "-1";
        }
        return rows;
    }

    /**
     * @param cmd 数据文件校验命令
     * @return 返回文件校验的字节码
     */
    public String[] runCKSUMN(String cmd) {

        // long code = 0;
        String inline;
        // String tmp = "";
        String[] tmps = null;
        try {
            this.process = this.runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(this.process
                    .getInputStream()));
            while (null != (inline = br.readLine())) {
                // tmp = inline.substring(0, inline.indexOf(" ")).trim();
                // code = Long.valueOf(tmp);
                tmps = inline.split(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }

        // return code;
        return tmps;

    }

    /**
     * @param cmd 数据文件校验命令
     * @return 返回文件校验的字节码
     */
    public Long runCKSUM(String cmd) {

        long code = 0;
        String inline;
        String tmp = "";
        // String [] tmps =null;
        try {
            this.process = this.runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(this.process
                    .getInputStream()));
            while (null != (inline = br.readLine())) {
                tmp = inline.substring(0, inline.indexOf(" ")).trim();
                code = Long.valueOf(tmp);
                // tmps = inline.split(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }

        return code;
        // return tmps;

    }

    /**
     * @param cmd 数据文件校验命令
     * @return 返回文件校验的字节码
     */
    public long runSize(String cmd) {

        long code = 0;
        String inline;
        String tmp[];
        // System.out.println(cmd);
        try {
            this.process = this.runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(this.process
                    .getInputStream()));
            while (null != (inline = br.readLine())) {
                tmp = inline.split(" ");
                System.out.println("****" + tmp[4]);
                code = Long.valueOf(tmp[4]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }

        return code;

    }


    /**
     * @param cmd 返回执行shell的日志信息
     * @return 返回执行结果
     */
    public String runString(String cmd) {

        String inline = "";
        String rtnStr = "";
        // System.out.println(cmd);
        try {
            this.process = this.runtime.exec(cmd);
            br = new BufferedReader(new InputStreamReader(this.process
                    .getInputStream()));
            while (null != (inline = br.readLine())) {
                rtnStr += inline;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }

        return rtnStr;

    }

    /**
     * 退出
     */
    public void exit() {

        // this.process.destroy();
        // this.runtime = null;
        // try {
        // this.br.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    public static void main(String args[]) {
        RunSH s = new RunSH();
        s.run("/Users/chendi/Desktop/start.sh");
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

}
