package dev.yanshouwang.codescanner.analyzers;

public interface IAnalyzedListener<T extends IAnalyzable> {
    void analyzed(T obj);
}
