package me.paulf.fairylights;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UpdateLang {
    public static void main(String[] args) throws Exception {
        ImmutableMap<String, String> map = ImmutableMap.<String, String>builder()
            .put("item.fairylights.fairy_light", "block.fairylights.fairy_light")
            .put("item.fairylights.paper_lantern", "block.fairylights.paper_lantern")
            .put("item.fairylights.orb_lantern", "block.fairylights.orb_lantern")
            .put("item.fairylights.flower_light", "block.fairylights.flower_light")
            .put("item.fairylights.ornate_lantern", "block.fairylights.ornate_lantern")
            .put("item.fairylights.oil_lantern", "block.fairylights.oil_lantern")
            .put("item.fairylights.jack_o_lantern", "block.fairylights.jack_o_lantern")
            .put("item.fairylights.skull_light", "block.fairylights.skull_light")
            .put("item.fairylights.ghost_light", "block.fairylights.ghost_light")
            .put("item.fairylights.spider_light", "block.fairylights.spider_light")
            .put("item.fairylights.witch_light", "block.fairylights.witch_light")
            .put("item.fairylights.snowflake_light", "block.fairylights.snowflake_light")
            .put("item.fairylights.icicle_lights", "block.fairylights.icicle_lights")
            .put("item.fairylights.meteor_light", "block.fairylights.meteor_light")
            .build();
        Pattern pattern = Pattern.compile(map.keySet().stream()
            .map(Pattern::quote)
            .collect(Collectors.joining("|")));
        Files.list(Paths.get("C:\\Users\\paulf\\Production\\Software\\Games\\Minecraft\\1.14\\Fairy-Lights\\src\\main\\resources\\assets\\fairylights\\lang"))
            .filter(p -> p.toString().endsWith(".json"))
            .forEach(p -> {
                StringBuffer buf = new StringBuffer();
                byte[] bytes;
                try {
                    bytes = Files.readAllBytes(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Matcher matcher = pattern.matcher(new String(bytes));
                while (matcher.find()) {
                    matcher.appendReplacement(buf, map.get(matcher.group()));
                }
                matcher.appendTail(buf);
                try {
                    Files.write(p, buf.toString().getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
