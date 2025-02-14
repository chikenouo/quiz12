package com.example.quiz12.entity;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "quiz")
public class Quiz {
    // @GeneratedValue 可參考投影片SpringBoot_01_基礎 的 P.26~27
    // 此 Annotation 使用上的差別在於若是使用 JAP 的 save 方法時，欄位是AI(Auto Incremental)時，//
    // 該欄位的資料型態:
    // 1. 是 int 時，可不加，但 save 方法的回傳值中，不會有最新的 AI 值，加了才會有最新的AI欄位值，//
    // 2. 是 Integer 時，一定要加，不然會報錯

    @GeneratedValue(strategy = GenerationType.IDENTITY) //有加才有內容不然會是0
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "published")
    private boolean published;

    public Quiz() {
    }

    public Quiz(String name, String description, LocalDate startDate, LocalDate endDate, boolean publish) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = publish;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isPublish() {
        return published;
    }

    public void setPublish(boolean publish) {
        this.published = publish;
    }
}
