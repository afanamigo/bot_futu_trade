package com.bottrade.bot;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.function.ConverterFileName;
import jakarta.validation.constraints.NotNull;
//import org.jetbrains.annotations.NotNull;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author JonSnow
 * @description TestUtil
 * @date 2022/9/27
 */
public class MybatisPlusUtil {

    public static void main(String[] args) {
        DataSourceConfig.Builder b = new DataSourceConfig.Builder("jdbc:mysql://209.182.237.103:3306/at_bot?autoReconnect=true&failOverReadOnly=false",
                "atbot", "atBot@520").typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int code = metaInfo.getJdbcType().TYPE_CODE;
                    if(code == Types.TINYINT){
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);
                });
        FastAutoGenerator.create(b)
                // 全局配置
                .globalConfig((scanner, builder) ->
                        builder.author(scanner.apply("请输入作者名称？"))
                                .disableServiceInterface()
                                .disableOpenDir() //禁止打开输出目录
                                .outputDir(System.getProperty("user.dir")+"/bot/src/main/java")
                )
                .templateConfig(builder -> builder.disable(TemplateType.CONTROLLER))
                // 包配置
                .packageConfig((scanner, builder) ->
                        builder.parent("com.bottrade.bot.dao")
                        .entity("entity")           //Entity 包名 默认值:entity
                        .mapper("mapper")           //Mapper 包名 默认值:mapper
                        .service("service")           //Mapper 包名 默认值:mapper
                        .serviceImpl("service")           //Mapper 包名 默认值:mapper
                )
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .entityBuilder().enableLombok().enableFileOverride().build()
                        .serviceBuilder().convertServiceImplFileName(new ConverterFileName() {
                            @Override
                            public @NotNull String convert(String entityName) {
                                return entityName+"Service";
                            }
                        }).enableFileOverride()
                        .mapperBuilder().enableFileOverride()
                )
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .templateEngine(new FreemarkerTemplateEngine())
//                .templateEngine()
                .execute();
    }

    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}
