# Project Rules: School Medical Management System

## Core Behavioral Constraints for AI Agents

1. **Plan-First Principle**:
   - Never implement code or modify files without first creating a detailed plan (`docs/plans/...` or `implementation_plan.md`).
   - You must obtain the user's explicit approval before proceeding to execution.

2. **Ask Before Deciding (Options & Recommendations)**:
   - If there are multiple implementation paths, technical trade-offs, or unexpected issues arising during execution:
     - DO NOT decide unilaterally.
     - You MUST ask the user.
     - When asking, clearly present the context, provide at least 2 viable options with Pros/Cons/Risks, and give a clear recommended choice with rationale.

3. **Proactive Multi-Option Solutions**:
   - You are allowed and actively encouraged to offer multiple optimal and alternative solutions (e.g., quick implementation vs. enterprise-grade scalability vs. balanced approach) for the user to compare, evaluate, and choose.
   - For each option, clearly explain the pros, cons, complexity, performance impact, and maintenance overhead, accompanied by your recommended choice and rationale.

4. **Git Commit & Push Discipline (Strict User Authorization Only)**:
   - **NO AUTOMATIC COMMITS**: NEVER automatically create git commits after executing commands, building, or modifying files. Automatic commits clutter the git history and make changes difficult to manage.
   - **ONLY COMMIT WHEN EXPLICITLY REQUESTED**: Only draft commit messages and execute `git commit` when the user explicitly requests it (e.g. "hãy commit", "soạn commit").
   - When requested: inspect changes (`git status`, `git diff`), propose a clear commit message following Conventional Commits, and commit upon approval.
   - **NO UNAUTHORIZED GIT PUSH**: NEVER run `git push` to remote repositories without explicit user permission/instruction.
   - Non-destructive inspection commands (`git status`, `git diff`, `git log`) are allowed at any time.

5. **Scope Discipline**:
   - Only modify files directly related to the approved task.
   - Do NOT clean up, reformat, or refactor unrelated adjacent code.
   - Preserve existing code comments and docstrings.

6. **Mandatory Verification Gate**:
   - Always verify changes with `.\mvnw clean test-compile` (or relevant test suites).
   - Only declare a step or phase complete when the build succeeds with 0 errors (`BUILD SUCCESS`).

7. **Security & Secrets Hygiene**:
   - Never hardcode passwords, emails, API keys, or database credentials.
   - Always use environment variables and ensure secret files (`.env`, `application-dev.properties`) are in `.gitignore`.

8. **Documentation Sync**:
   - Always keep `docs/plans/master-upgrade-plan.md` updated with progress.
   - Provide manual verification steps for the user after completing each phase.
