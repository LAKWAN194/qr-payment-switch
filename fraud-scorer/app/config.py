from pathlib import Path

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Service settings, read from environment variables prefixed with FRAUD_."""

    model_config = SettingsConfigDict(env_prefix="FRAUD_", env_file=".env", extra="ignore")

    # Where the trained model will live (week 5). The service starts without one.
    model_path: Path = Path("training/artifacts/model.json")


settings = Settings()
