package com.ericlam.qqbot.valbot.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.ericlam.qqbot.valbot.filter.ForwardToDiscordFilter;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.dto.action.common.ActionList;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class TestApplicationTest {

    void convertToActionList() {
        String json = "{\"data\":[1,2,3],\"echo\":1,\"retcode\":0,\"status\":\"ok\"}";
        JSONObject o = (JSONObject) JSONObject.parse(json);
        System.out.println(o.get("data"));
        ActionList<Integer> obj = toObject(o); // can't
        obj.getData().forEach(e -> System.out.println(e.getClass()));
    }


    void testGetMultipleImagesFromCQ() {
        var cqCode = "[CQ:image,file=899662e96b7fecdbb7b0dff3dce3e105.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3275362519-899662E96B7FECDBB7B0DFF3DCE3E105/0?term=2][CQ:image,file=8e60005cd08dca91bec6f3eabd535a01.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1510541511-8E60005CD08DCA91BEC6F3EABD535A01/0?term=2][CQ:image,file=f1faf1bfdc28bbd21ae66cdf28ee2088.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-4160647449-F1FAF1BFDC28BBD21AE66CDF28EE2088/0?term=2][CQ:image,file=faf1b0aefb568b67e9e3f0be8376c949.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-426014759-FAF1B0AEFB568B67E9E3F0BE8376C949/0?term=2][CQ:image,file=723fe3aca7d96f978205a9b937f729bc.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3345208502-723FE3ACA7D96F978205A9B937F729BC/0?term=2][CQ:image,file=06d715d951e3d0049c780d6b08b87ac7.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3551207181-06D715D951E3D0049C780D6B08B87AC7/0?term=2][CQ:image,file=40ed671a5f4e4b88d760b212f1ccb5f2.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1142569616-40ED671A5F4E4B88D760B212F1CCB5F2/0?term=2][CQ:image,file=3367b669a81e4e6a13c20aefeae089d4.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3881067854-3367B669A81E4E6A13C20AEFEAE089D4/0?term=2][CQ:image,file=44c0353fc696d377a0540d174a4041b2.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1115271161-44C0353FC696D377A0540D174A4041B2/0?term=2][CQ:image,file=ebf6e39c1ddd8f41825c306a20001c48.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-351464469-EBF6E39C1DDD8F41825C306A20001C48/0?term=2][CQ:image,file=3f8ef5046fd8a6c8723aa430121504c3.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3308808561-3F8EF5046FD8A6C8723AA430121504C3/0?term=2][CQ:image,file=18528dfbc59ebd547b611c63afe962cd.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3741381735-18528DFBC59EBD547B611C63AFE962CD/0?term=2][CQ:image,file=3fb7a735ce04bad7de1399c78695588d.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1045849007-3FB7A735CE04BAD7DE1399C78695588D/0?term=2][CQ:image,file=3a0cc519e5d8e3af3c907da90c3c2638.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-902121463-3A0CC519E5D8E3AF3C907DA90C3C2638/0?term=2][CQ:image,file=da87f849eed410eabbc3703aab7f6682.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-21401648-DA87F849EED410EABBC3703AAB7F6682/0?term=2][CQ:image,file=bf8369ef218809b8ebafa06d9cd9c70e.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-718221559-BF8369EF218809B8EBAFA06D9CD9C70E/0?term=2][CQ:image,file=caf95449a971a9425082417caa3b9d1f.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1487676641-CAF95449A971A9425082417CAA3B9D1F/0?term=2][CQ:image,file=50eee7caabef3ad07c2e905276ef218d.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-4015816788-50EEE7CAABEF3AD07C2E905276EF218D/0?term=2][CQ:image,file=1bac5612b49a72cf909c498f1c2bf509.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3731150862-1BAC5612B49A72CF909C498F1C2BF509/0?term=2][CQ:image,file=e2f0e5628d51e2187fef8c0da509a0eb.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1944361484-E2F0E5628D51E2187FEF8C0DA509A0EB/0?term=2][CQ:image,file=3a500f31b0a1afd697c20b8ddf8611a2.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-2228923932-3A500F31B0A1AFD697C20B8DDF8611A2/0?term=2][CQ:image,file=6f9bd166b7f957ffec62b48f6c9722fd.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-2136460185-6F9BD166B7F957FFEC62B48F6C9722FD/0?term=2][CQ:image,file=1cf141cdba9234db869c0253a85b733d.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-2741252177-1CF141CDBA9234DB869C0253A85B733D/0?term=2][CQ:image,file=e88de8ede8f7dfaa0fd7f3ffaf2ac2a4.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-2789866163-E88DE8EDE8F7DFAA0FD7F3FFAF2AC2A4/0?term=2][CQ:image,file=be3cd4cae45d2055199782b6f6466808.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3122427008-BE3CD4CAE45D2055199782B6F6466808/0?term=2][CQ:image,file=0588fabfd20a7e1ffb16d5ca7690ccee.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3597300605-0588FABFD20A7E1FFB16D5CA7690CCEE/0?term=2][CQ:image,file=da8bd2a659e2a9d28e319c7b4efc349f.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-2053318129-DA8BD2A659E2A9D28E319C7B4EFC349F/0?term=2][CQ:image,file=67f9a80aded44b33ca8a93e9739e9453.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-856177275-67F9A80ADED44B33CA8A93E9739E9453/0?term=2][CQ:image,file=b1271b5c0d5ec04834e31447b9fe3ab6.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-2528293649-B1271B5C0D5EC04834E31447B9FE3AB6/0?term=2][CQ:image,file=9ad4ff43ad2f841c202674b389d61590.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-2486987853-9AD4FF43AD2F841C202674B389D61590/0?term=2][CQ:image,file=74e5dcdbb4a8e2c210b4d9a0328558bc.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-2889011060-74E5DCDBB4A8E2C210B4D9A0328558BC/0?term=2][CQ:image,file=3475e56a96f1a369129aa3d2c5906b78.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1215610762-3475E56A96F1A369129AA3D2C5906B78/0?term=2][CQ:image,file=3d89f56b04c35c42d5f35112c72e0f1e.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3408776937-3D89F56B04C35C42D5F35112C72E0F1E/0?term=2][CQ:image,file=a4840b4e828ddcd1045c1b3143d08c4e.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3146831385-A4840B4E828DDCD1045C1B3143D08C4E/0?term=2][CQ:image,file=4d2881974d302b49f0899eaab08b8f63.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1922538266-4D2881974D302B49F0899EAAB08B8F63/0?term=2][CQ:image,file=46f74415f77c2776ad84cf6177702730.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-101318926-46F74415F77C2776AD84CF6177702730/0?term=2][CQ:image,file=46f74415f77c2776ad84cf6177702730.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-624813500-46F74415F77C2776AD84CF6177702730/0?term=2][CQ:image,file=70ea57ac2c6bfee2f6aef00525f8ea9f.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3506373770-70EA57AC2C6BFEE2F6AEF00525F8EA9F/0?term=2][CQ:image,file=20ab314e3b3d1c77ee588fb73b9579dd.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3281092835-20AB314E3B3D1C77EE588FB73B9579DD/0?term=2][CQ:image,file=4c68d833ba1bc97be115eb28d209b5af.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1322710400-4C68D833BA1BC97BE115EB28D209B5AF/0?term=2][CQ:image,file=cea1a43fe5abb512d895ae8f229b686e.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3269545316-CEA1A43FE5ABB512D895AE8F229B686E/0?term=2][CQ:image,file=ef300e92dda165a0f383f63bffb04eff.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1018576409-EF300E92DDA165A0F383F63BFFB04EFF/0?term=2]";
        var cqCode2 = "[CQ:image,file=899662e96b7fecdbb7b0dff3dce3e105.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-3275362519-899662E96B7FECDBB7B0DFF3DCE3E105/0?term=2][CQ:image,file=8e60005cd08dca91bec6f3eabd535a01.image,url=https://c2cpicdw.qpic.cn/offpic_new/0//2899929243-1510541511-8E60005CD08DCA91BEC6F3EABD535A01/0?term=2]";
        ForwardToDiscordFilter.getMsgImgUrlList(cqCode2).forEach(System.out::println);
    }

    public <T> T toObject(JSONObject o){
        return o.toJavaObject(new TypeReference<T>(){});
    }


    @Getter
    @Setter
    public static class MyActionList<T> extends ActionList<T> {

        @JSONField(name = "echo")
        private int echo;

    }
}