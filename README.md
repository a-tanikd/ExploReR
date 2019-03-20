# ExploReR
ExploReR is a tool that refactors source code to improve understandability by reordering local variable declarations.


## Requirements
- JDK8


## Usage
```
$ java -jar path/to/ExploReR.jar [(-r <path> -s <path>... -t <path>...) | --config <path>]
    --refactored-method <method name> [-x <fqn>...] [-c <path>...] [-w <path>] [-o <path>]
    [-v | -q] [--siblings-count <num>] [--headcount <num>] [--max-generation <num>]
    [--time-limit <sec>] [--test-time-limit <sec>] [--required-solutions <num>] [--random-seed <num>]
```


### Options
| Option | Description | Default |
|---|---|---|
| `-r`, `--root-dir` | Specifies the path to the root directory of the target project. It is recommended to specify the current directory after moving into the root directory of the target project, for implementation reason. | Nothing |
| `-s`, `--src` | Specifies paths to "product" source code (i.e. main, non-test code), or to directories containing them. Paths are separated with spaces. | Nothing |
| `-t`, `--test` | Specifies paths to test source code, or to directories containing them. Paths are separated with spaces. | Nothing |
| `--refactored-method` | Specifies the method name to be refactored. | Nothing |
| `-x`, `--exec-test` | Specifies fully qualified names of test classes executed during evaluation of variants (i.e. refactoring-candidates). Class names are separated with spaces. | All test classes |
| `-c`, `--cp` | Specifies class paths needed to build the target project. Paths are separated with spaces. | Nothing |
| `-o`, `--out-dir` | Writes patches ExploReR generated under the specified directory. Patches are outputted to a directory having a name of the execution time and date under the specified directory. | A directory named `kgenprog-out` is created in the current directory. |
| `-v`, `--verbose` | Be more verbose, printing DEBUG level logs. | `false` |
| `-q`, `--quiet` | Be more quiet, suppressing non-ERROR logs. | `false` |
| `--mutation-generating-count` | Specifies how many variants are generated in a generation by a mutation. | 10 |
| `--crossover-generating-count` | Specifies how many variants are generated in a generation by a crossover. | 10 |
| `--headcount` | Specifies how many variants survive in a generation. | 100 |
| `--max-generation` | Terminates searching solutions when the specified number of generations reached. | 10 |
| `--time-limit` | Terminates searching solutions when the specified time in seconds has passed. | 60 |
| `--test-time-limit` | Specifies a time limit in seconds to build and test each variant. | 10 |
| `--required-solutions` | Terminates searching solutions when the specified number of solutions are found. | 1 |
| `--random-seed` | Specifies a random seed used by a random number generator. | 0 |
| `--scope` | Specify the scope from which source code to be reused is selected. (`PROJECT`, `PACKAGE`, `FILE`). | `PACKAGE` |

