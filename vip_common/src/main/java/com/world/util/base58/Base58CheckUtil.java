package com.world.util.base58;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Base58CheckUtil {
    private static final int CHECKSUM_LENGTH = 4;

    public static Base58CheckContents parseBase58Check(final String base58Check) {
        final byte[] decode = Base58.decode(base58Check);

        final Base58CheckContents contents = new Base58CheckContents();

        contents.setVersion(decode[0]);
        contents.setChecksum(extractChecksum(decode));
        contents.setPayload(ArrayUtil.arrayCopy(decode, 1, decode.length - CHECKSUM_LENGTH));

        return contents;
    }

    public static byte[] computeChecksum(final byte version, final byte[] payload) {
        final byte[] checksum = ComputeUtil.computeDoubleSHA256(new byte[] { version }, payload);

        return ArrayUtil.arrayCopy(checksum, 0, CHECKSUM_LENGTH);
    }

    public static boolean isValid(final Base58CheckContents base58contents) {
        return Arrays.areEqual(base58contents.getChecksum(), computeChecksum(base58contents.getVersion(), base58contents.getPayload()));
    }

    /**
     * Extract the checksum from some thing that is a base58check format
     * @param thing thing checksum is extracted form
     * @return last 4 bytes of thing
     */
    private static byte[] extractChecksum(final byte[] thing) {
        return ArrayUtil.arrayCopy(thing, thing.length - CHECKSUM_LENGTH, thing.length);
    }
    public static  Map<String,BigDecimal> ERC20_TOKEN_UNIT_MAP = new HashMap<String,BigDecimal>();
    public static final Map<String, String> checkCoinAddrMap = new HashMap<>();

    static{
        checkCoinAddrMap.put("btc","00");
        checkCoinAddrMap.put("dash","4c");
        checkCoinAddrMap.put("zec","1c");
        checkCoinAddrMap.put("ltc","30");
        checkCoinAddrMap.put("btg","26");
        checkCoinAddrMap.put("bch","00");
        checkCoinAddrMap.put("bth","28");
        checkCoinAddrMap.put("sbtc","00");
        checkCoinAddrMap.put("qtum","3a");
        checkCoinAddrMap.put("usdt","00");
        checkCoinAddrMap.put("vds","10");

        ERC20_TOKEN_UNIT_MAP.put("OMG",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("GBC",new BigDecimal(1E8));
        ERC20_TOKEN_UNIT_MAP.put("BAT",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("SNT",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("LRC",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("ELF",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("REP",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("KNC",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("LINK",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("MCO",new BigDecimal(1E8));
        ERC20_TOKEN_UNIT_MAP.put("DGD",new BigDecimal(1E9));
        ERC20_TOKEN_UNIT_MAP.put("USDTE",new BigDecimal(1E6));

        ERC20_TOKEN_UNIT_MAP.put("CVC",new BigDecimal(1E8));
        ERC20_TOKEN_UNIT_MAP.put("IOST",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("MANA",new BigDecimal(1E18));
        ERC20_TOKEN_UNIT_MAP.put("ZRX",new BigDecimal(1E18));


    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static boolean isHexNumber(String str){
        boolean flag = false;
        for(int i=0;i<str.length();i++){
            char cc = str.charAt(i);
            if(cc=='0'||cc=='1'||cc=='2'||cc=='3'||cc=='4'||cc=='5'||cc=='6'||cc=='7'||cc=='8'||cc=='9'||cc=='A'||cc=='B'||cc=='C'||
                    cc=='D'||cc=='E'||cc=='F'||cc=='a'||cc=='b'||cc=='c'||cc=='c'||cc=='d'||cc=='e'||cc=='f'){
                flag = true;
            }
        }
        return flag;
    }

}
