<script lang="ts">
  interface Props {
    title: string,
    time: string
  }

  let {
    title,
    time = $bindable(),
  }: Props = $props()
</script>

<div>
    <label class="mb-1 block text-sm font-medium text-slate-700" for="startTime">{title}</label>
    <input
            id="startTime"
            bind:value={time}
            placeholder="2026-08-20T10:00:00Z"
            required
            class="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none ring-indigo-200 transition focus:border-indigo-500 focus:ring"
    />
</div>
