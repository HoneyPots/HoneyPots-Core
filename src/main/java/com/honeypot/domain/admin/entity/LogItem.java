package com.honeypot.domain.admin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "log_date_time")
    private LocalDateTime logDateTime;

    @Column(name = "level")
    private String level;

    @Column(name = "process_id")
    private int processId;

    @Column(name = "thread_name")
    private String threadName;

    @Column(name = "logger")
    private String logger;

    @Column(name = "message", length = 10000)
    private String message;

}