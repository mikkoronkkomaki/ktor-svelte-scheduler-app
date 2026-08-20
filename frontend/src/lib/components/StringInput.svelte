<script lang="ts">
  interface Props {
    id: string
    title: string,
    value: string,
    placeholder: string
  }
  let {
    id,
    title,
    value = $bindable(),
    placeholder,
  }: Props = $props()
</script>

<div>
    <label class="mb-1 block text-sm font-medium text-slate-700" for="description">{title}</label>
    <input
            id={id}
            bind:value={value}
            placeholder={placeholder}
            required
            class="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none ring-indigo-200 transition focus:border-indigo-500 focus:ring"
    />
</div>
