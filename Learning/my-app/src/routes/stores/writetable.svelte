<script>
    import { count } from "./store";

    function increment() {
        count.update((n) => n + 1);
    }

    function decrement() {
        count.update((n) => n - 1);
    }

    function reset() {
        count.set(0);
    }

    // custom store
    import { countCustom } from "./store";
</script>

<div class="btn">
    <button on:click={increment}>Increment</button>
    <button on:click={decrement}>Decrement</button>
    <button on:click={reset}>Reset</button>
</div>

<div class="btn">
    <button on:click={countCustom.increment}>Increment</button>
    <button on:click={countCustom.decrement}>Decrement</button>
    <button on:click={countCustom.reset}>Reset</button>
</div>

<style>
    .btn {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin-bottom: 2em;
    }
</style>
