package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

public class PatchGenerator implements ResultGenerator {

  private static Logger log = LoggerFactory.getLogger(PatchGenerator.class);
  private final Path workingDir;

  public PatchGenerator(Path workingDir) {
    this.workingDir = workingDir;
  }

  @Override
  public void exec(final TargetProject targetProject, final List<Variant> modifiedVariants) {

    final List<GeneratedSourceCode> modifiedCode = new ArrayList<>();

    // 出力先ディレクトリ作成
    if (!Files.exists(workingDir)) {
      try {
        Files.createDirectory(workingDir);
      } catch (IOException e) {
        // TODO 自動生成された catch ブロック
        e.printStackTrace();
      }
    }

    modifiedCode.addAll(applyAllModificationDirectly(targetProject, modifiedVariants));

    for (GeneratedSourceCode code : modifiedCode) {
      final Path variantBasePath = makeDirName(modifiedCode.indexOf(code) + 1);

      try {
        Files.createDirectory(variantBasePath);
      } catch (IOException e1) {
        // TODO 自動生成された catch ブロック
        e1.printStackTrace();
      }
      for (GeneratedAST ast : code.getAsts()) {
        try {
          final GeneratedJDTAST jdtAST = (GeneratedJDTAST) ast;
          final Path originPath = jdtAST.getProductSourcePath().path;

          // 修正ファイル作成
          final Document document = new Document(new String(Files.readAllBytes(originPath)));
          final TextEdit edit = jdtAST.getRoot()
              .rewrite(document, null);
          // その AST が変更されているかどうか判定
          if (edit.getChildren().length != 0) {
            final Path diffFilePath =
                variantBasePath.resolve(jdtAST.getPrimaryClassName() + ".java");
            edit.apply(document);
            Files.write(diffFilePath, Arrays.asList(document.get()));

            makePatchFile(originPath, diffFilePath,
                variantBasePath.resolve(jdtAST.getPrimaryClassName() + ".patch"));
          }
        } catch (MalformedTreeException e) {
          e.printStackTrace();
        } catch (BadLocationException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * variant 内のすべての AST が，その AST に対して行われた変更履歴を記録するように設定．
   *
   * @param variant
   */
  private void activateRecordModifications(GeneratedSourceCode code) {
    for (GeneratedAST ast : code.getAsts()) {
      ((GeneratedJDTAST) ast).getRoot()
          .recordModifications();
    }
  }

  /***
   * 初期 ast に対して，修正された ast へ実行された全変更内容をクローンを生成せずに適用
   *
   * @param targetProject
   * @param modifiedVariants
   * @return
   */
  private List<GeneratedSourceCode> applyAllModificationDirectly(TargetProject targetProject,
      List<Variant> modifiedVariants) {
    final List<GeneratedSourceCode> modifiedSourceCode = new ArrayList<>();

    for (Variant variant : modifiedVariants) {
      GeneratedSourceCode targetCode = targetProject.getInitialVariant()
          .getGeneratedSourceCode();
      activateRecordModifications(targetCode);
      for (Base base : variant.getGene()
          .getBases()) {
        targetCode = base.getOperation()
            .applyDirectly(targetCode, base.getTargetLocation());
      }
      modifiedSourceCode.add(targetCode);
    }

    return modifiedSourceCode;
  }

  /***
   * originPath と diffFile の間のパッチを patchFile へ出力する
   *
   * @param originPath
   * @param diffPath
   * @param patchPath
   */
  private void makePatchFile(Path originPath, Path diffPath, Path patchPath) {
    try {
      final List<String> origin = Files.readAllLines(originPath);
      final List<String> modifiedSourceCode = Files.readAllLines(diffPath);

      Patch<String> diff = DiffUtils.diff(origin, modifiedSourceCode);

      String fileName = originPath.getFileName()
          .toString();

      final List<String> unifiedDiff =
          UnifiedDiffUtils.generateUnifiedDiff(fileName, fileName, origin, diff, 3);

      unifiedDiff.forEach(e -> log.info(e));

      Files.write(patchPath, unifiedDiff);
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (DiffException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }

  /***
   * 出力ディレクトリ名の生成
   *
   * @param variantNum
   * @return
   */
  private Path makeDirName(int variantNum) {
    return workingDir.resolve("variant" + variantNum);
  }
}
