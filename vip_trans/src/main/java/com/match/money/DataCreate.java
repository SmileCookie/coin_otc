package com.match.money;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/12/11 11:47 AM
 */
public class DataCreate {
    public static void main(String[] args) {
//        one(5000);
//        one(10000);
//        one(20000);
//        one(50000);
//        one(100000);
//
//        diff(5000);
//        diff(10000);
//        diff(20000);
//        diff(50000);
//        diff(100000);
//
//        mix(5000);
//        mix(10000);
//        mix(20000);
//        mix(50000);
//        mix(100000);

//        entrust(100);

        brush2(10000);
    }

    public static void one(int count) {
        try {
            File file = new File("/Users/buxianguan/Documents/match/one_transRecord_" + count + ".sql");
            Writer out = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(out);
            bw.write("truncate table transrecord;");
            bw.newLine();

            for (int i = 1; i < count; i++) {
                String transRecordSql = "INSERT INTO `transrecord`(`unitPrice`, `totalPrice`, `numbers`, `entrustIdBuy`, `userIdBuy`, `entrustIdSell`, `userIdSell`, `types`, `times`, `timeMinute`, `status`, `feesBuy`, `feesSell`, `isCount`, `webIdBuy`, `webIdSell`, `dealTimes`, `actStatus`) " +
                        "VALUES (1.000000000, 1.000000000, 1.000000000, %s, 1, %s, 1, 0, 1542254292882, 1542254280000, 2, 0.000000000, 0.000000000, 0, 0, 8, 0, 0);";
                transRecordSql = String.format(transRecordSql, i, i + 1);

                bw.write(transRecordSql);
                bw.newLine();

            }
            bw.flush();
            bw.close();


            File file2 = new File("/Users/buxianguan/Documents/match/one_payUser_" + count + ".sql");
            Writer out2 = new FileWriter(file2);
            BufferedWriter bw2 = new BufferedWriter(out2);
            bw2.write("truncate table pay_user;");
            bw2.newLine();

            String payUserOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                    "VALUES (1, 'test', 100000000.000000000, 100000000.000000000, 10);";
            String payUserTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                    "VALUES (1, 'test', 100000000.000000000, 100000000.000000000, 2);";

            bw2.write(payUserOne);
            bw2.newLine();

            bw2.write(payUserTwo);
            bw2.newLine();

            bw2.flush();
            bw2.close();

            System.out.println("结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void diff(int count) {
        try {
            File file = new File("/Users/buxianguan/Documents/match/diff_transRecord_" + count + ".sql");
            Writer out = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(out);
            bw.write("truncate table transrecord;");
            bw.newLine();

            File file2 = new File("/Users/buxianguan/Documents/match/diff_payUser_" + count + ".sql");
            Writer out2 = new FileWriter(file2);
            BufferedWriter bw2 = new BufferedWriter(out2);
            bw2.write("truncate table pay_user;");
            bw2.newLine();

            for (int i = 1; i < count * 2; i = i + 2) {
                String transRecordSql = "INSERT INTO `transrecord` (`unitPrice`, `totalPrice`, `numbers`, `entrustIdBuy`, `userIdBuy`, `entrustIdSell`, `userIdSell`, `types`, `times`, `timeMinute`, `status`, `feesBuy`, `feesSell`, `isCount`, `webIdBuy`, `webIdSell`, `dealTimes`, `actStatus`) " +
                        "VALUES (1.000000000, 1.000000000, 1.000000000, %s, %s, %s, %s, 0, 1542254292882, 1542254280000, 2, 0.000000000, 0.000000000, 0, 0, 8, 0, 0);";
                transRecordSql = String.format(transRecordSql, i, i, i + 1, i + 1);

                String payUserBuyOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 100.000000000, 100.000000000, 10);";
                payUserBuyOne = String.format(payUserBuyOne, i);
                String payUserBuyTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 0.000000000, 0.000000000, 2);";
                payUserBuyTwo = String.format(payUserBuyTwo, i);

                String payUserSell = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 100.000000000, 100.000000000, 2);";
                payUserSell = String.format(payUserSell, i + 1);
                String payUserSellTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 0.000000000, 0.000000000, 10);";
                payUserSellTwo = String.format(payUserSellTwo, i + 1);

                bw.write(transRecordSql);
                bw.newLine();

                bw2.write(payUserBuyOne);
                bw2.newLine();

                bw2.write(payUserBuyTwo);
                bw2.newLine();

                bw2.write(payUserSell);
                bw2.newLine();

                bw2.write(payUserSellTwo);
                bw2.newLine();
            }
            bw.flush();
            bw.close();

            bw2.flush();
            bw2.close();

            System.out.println("结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mix(int count) {
        try {
            File file = new File("/Users/buxianguan/Documents/match/mix_transRecord_" + count + ".sql");
            Writer out = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(out);
            bw.write("truncate table transrecord;");
            bw.newLine();

            File file2 = new File("/Users/buxianguan/Documents/match/mix_payUser_" + count + ".sql");
            Writer out2 = new FileWriter(file2);
            BufferedWriter bw2 = new BufferedWriter(out2);
            bw2.write("truncate table pay_user;");
            bw2.newLine();

            for (int i = 1; i < count; i++) {
                String transRecordSql = "INSERT INTO `transrecord` (`unitPrice`, `totalPrice`, `numbers`, `entrustIdBuy`, `userIdBuy`, `entrustIdSell`, `userIdSell`, `types`, `times`, `timeMinute`, `status`, `feesBuy`, `feesSell`, `isCount`, `webIdBuy`, `webIdSell`, `dealTimes`, `actStatus`) " +
                        "VALUES (1.000000000, 1.000000000, 1.000000000, %s, %s, %s, %s, 0, 1542254292882, 1542254280000, 2, 0.000000000, 0.000000000, 0, 0, 8, 0, 0);";
                transRecordSql = String.format(transRecordSql, i, i, i + 1, i + 1);

                bw.write(transRecordSql);
                bw.newLine();

                String payUserOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 100.000000000, 100.000000000, 10);";
                String payUserTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 100.000000000, 100.000000000, 2);";
                payUserOne = String.format(payUserOne, i);
                payUserTwo = String.format(payUserTwo, i);

                bw2.write(payUserOne);
                bw2.newLine();

                bw2.write(payUserTwo);
                bw2.newLine();
            }
            bw.flush();
            bw.close();


            String payUserOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                    "VALUES (%s, 'test', 100000000.000000000, 100000000.000000000, 10);";
            String payUserTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                    "VALUES (%s, 'test', 100000000.000000000, 100000000.000000000, 2);";
            payUserOne = String.format(payUserOne, count);
            payUserTwo = String.format(payUserTwo, count);

            bw2.write(payUserOne);
            bw2.newLine();

            bw2.write(payUserTwo);
            bw2.newLine();

            bw2.flush();
            bw2.close();

            System.out.println("结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void entrust(int count) {
        try {
            File file = new File("/Users/buxianguan/Documents/match/entrust_" + count + ".sql");
            Writer out = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(out);
            bw.write("truncate table entrust;");
            bw.newLine();

            File file2 = new File("/Users/buxianguan/Documents/match/entrust_payUser_" + count + ".sql");
            Writer out2 = new FileWriter(file2);
            BufferedWriter bw2 = new BufferedWriter(out2);
            bw2.write("truncate table pay_user;");
            bw2.newLine();

            for (int i = 1; i < count * 2; i = i + 2) {
                String entrustSql = "INSERT INTO `entrust` (`entrustId`, `unitPrice`, `numbers`, `totalMoney`, `types`, `userId`, `status`, `submitTime`) " +
                        "VALUES (%s, %s, 1, %s, %s, %s, 0, 1541555957408);";
                String entrustBuySql = String.format(entrustSql, i, i, i, 1, i);
                String entrustSellSql = String.format(entrustSql, i + 1, i, i, 0, i + 1);

                bw.write(entrustBuySql);
                bw.newLine();
                bw.write(entrustSellSql);
                bw.newLine();

                String payUserBuyOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 10000000.000000000, 10000000.000000000, 10);";
                String payUserSellOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 10000000.000000000, 10000000.000000000, 2);";
                String payUserSellOne2 = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 10000000.000000000, 10000000.000000000, 6);";
                payUserBuyOne = String.format(payUserBuyOne, i);
                payUserSellOne = String.format(payUserSellOne, i);
                payUserSellOne2 = String.format(payUserSellOne2, i);

                bw2.write(payUserBuyOne);
                bw2.newLine();

                bw2.write(payUserSellOne);
                bw2.newLine();

                bw2.write(payUserSellOne2);
                bw2.newLine();

                String payUserBuyTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 10000000.000000000, 10000000.000000000, 10);";
                String payUserSellTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 10000000.000000000, 10000000.000000000, 2);";
                String payUserSellTwo2 = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, 'test', 10000000.000000000, 10000000.000000000, 6);";
                payUserBuyTwo = String.format(payUserBuyTwo, i + 1);
                payUserSellTwo = String.format(payUserSellTwo, i + 1);
                payUserSellTwo2 = String.format(payUserSellTwo2, i + 1);

                bw2.write(payUserBuyTwo);
                bw2.newLine();

                bw2.write(payUserSellTwo);
                bw2.newLine();

                bw2.write(payUserSellTwo2);
                bw2.newLine();
            }
            bw.flush();
            bw.close();

            bw2.flush();
            bw2.close();

            System.out.println("结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void brush(int count) {
        try {
            File file = new File("/Users/buxianguan/Documents/match/entrust_" + count + ".sql");
            Writer out = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(out);
            bw.write("truncate table entrust;");
            bw.newLine();

            File file2 = new File("/Users/buxianguan/Documents/match/entrust_payUser_" + count + ".sql");
            Writer out2 = new FileWriter(file2);
            BufferedWriter bw2 = new BufferedWriter(out2);
            bw2.write("truncate table pay_user;");
            bw2.newLine();

            for (int i = 1; i <= count; i++) {
                String entrustSql = "INSERT INTO `entrust` (`entrustId`, `unitPrice`, `numbers`, `totalMoney`, `types`, `userId`, `status`, `submitTime`) " +
                        "VALUES (%s, 1, 1, 1, %s, %s, 0, 1541555957408);";
                String entrustSellSql = String.format(entrustSql, i, 0, i);
                bw.write(entrustSellSql);
                bw.newLine();

                String payUserSellOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, %s, 0.000000000, 1.000000000, 2);";
                payUserSellOne = String.format(payUserSellOne, i, i);

                String payUserSellTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, %s, 0.000000000, 0.000000000, 10);";
                payUserSellTwo = String.format(payUserSellTwo, i, i);

                bw2.write(payUserSellOne);
                bw2.newLine();

                bw2.write(payUserSellTwo);
                bw2.newLine();
            }


            String entrustSql = "INSERT INTO `entrust` (`entrustId`, `unitPrice`, `numbers`, `totalMoney`, `types`, `userId`, `status`, `submitTime`) " +
                    "VALUES (%s, 1, %s, %s, %s, %s, 0, 1541555957408);";
            String entrustSellSql = String.format(entrustSql, count + 1, count, count, 1, count + 1);
            bw.write(entrustSellSql);
            bw.newLine();

            String payUserSellOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                    "VALUES (%s, %s, 0.000000000, %s, 10);";
            payUserSellOne = String.format(payUserSellOne, count + 1, count + 1, count);

            String payUserSellTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                    "VALUES (%s, %s, 0.000000000, 0.000000000, 2);";
            payUserSellTwo = String.format(payUserSellTwo, count + 1, count + 1, count);

            bw2.write(payUserSellOne);
            bw2.newLine();

            bw2.write(payUserSellTwo);
            bw2.newLine();

            bw.flush();
            bw.close();

            bw2.flush();
            bw2.close();

            System.out.println("结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void brush2(int count) {
        try {
            File file = new File("/Users/buxianguan/Documents/match/entrust_" + count + ".sql");
            Writer out = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(out);
            bw.write("truncate table entrust;");
            bw.newLine();

            File file2 = new File("/Users/buxianguan/Documents/match/entrust_payUser_" + count + ".sql");
            Writer out2 = new FileWriter(file2);
            BufferedWriter bw2 = new BufferedWriter(out2);
            bw2.write("truncate table pay_user;");
            bw2.newLine();

            for (int i = 1; i <= count; i++) {
                String entrustSql = "INSERT INTO `entrust` (`entrustId`, `unitPrice`, `numbers`, `totalMoney`, `types`, `userId`, `status`, `submitTime`) " +
                        "VALUES (%s, 1000, 1, 1000, %s, %s, 0, 1541555957408);";
                String entrustSellSql = String.format(entrustSql, i, 0, i);
                bw.write(entrustSellSql);
                bw.newLine();

                String payUserSellOne = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, %s, 0.000000000, 1000, 2);";
                payUserSellOne = String.format(payUserSellOne, i, i, i);

                String payUserSellTwo = "INSERT INTO `pay_user` (`userId`, `userName`, `balance`, `freez`, `fundsType`) " +
                        "VALUES (%s, %s, 0.000000000, 0.000000000, 10);";
                payUserSellTwo = String.format(payUserSellTwo, i, i);

                bw2.write(payUserSellOne);
                bw2.newLine();

                bw2.write(payUserSellTwo);
                bw2.newLine();
            }

            bw.flush();
            bw.close();

            bw2.flush();
            bw2.close();

            System.out.println("结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
