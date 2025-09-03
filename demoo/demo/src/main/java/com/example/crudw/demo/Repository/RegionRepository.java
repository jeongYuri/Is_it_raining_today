package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Weather.Region;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.boot.ApplicationRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
/*
@Repository
@Component
public class RegionRepository {

    @PersistenceContext
    private EntityManager em;

    private final RegionRepository self;

    public RegionRepository(@Lazy RegionRepository self) {
        this.self = self;
    }

    // ApplicationRunner를 사용하여 Spring이 완전히 실행된 후 실행
    public ApplicationRunner initRegionData() {
        System.out.println("🚀 ApplicationRunner 실행됨!");
        return args -> self.resetRegionList();
    }

    @Transactional
    public void resetRegionList() {
        String fileLocation = "init/weather_code.csv";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource(fileLocation).getInputStream()))) {
            System.out.println("📂 파일 로딩 성공! 경로: " + fileLocation);
            String line;
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(",");
                if (splits.length < 2) continue;

                String id = splits[0];
                String code = splits[1];

                // 기존 데이터 확인 (JPQL 사용)
                Long count = em.createQuery("SELECT COUNT(r) FROM Region r WHERE r.id = :id", Long.class)
                        .setParameter("id", id)
                        .getSingleResult();

                if (count == 0) {  // 존재하지 않으면 삽입
                    em.persist(new Region(id, code));
                }
            }
            em.flush();
            em.clear();
        } catch (IOException e) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + fileLocation, e);
        }

        System.out.println("✅ DB 초기화 완료!");
    }
    @Transactional
    public void saveRegions(List<Region> regions) {
        for (Region region : regions) {
            em.persist(region);
        }
        em.flush();
    }
}
*/