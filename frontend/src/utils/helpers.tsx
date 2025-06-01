export function isValidDate(value: any): value is Date | string {
  const date = new Date(value);
  return value && !isNaN(date.getTime());
}